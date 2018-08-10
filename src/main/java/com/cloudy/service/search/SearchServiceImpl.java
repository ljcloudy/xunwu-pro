package com.cloudy.service.search;

import com.cloudy.base.HouseSort;
import com.cloudy.base.RentValueBlock;
import com.cloudy.entity.House;
import com.cloudy.entity.HouseDetail;
import com.cloudy.entity.HouseTag;
import com.cloudy.repository.HouseDetailRepository;
import com.cloudy.repository.HouseRepository;
import com.cloudy.repository.HouseTagRepository;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.form.MapSearch;
import com.cloudy.web.form.RentSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/7/28.
 */
@Service
public class SearchServiceImpl implements SearchService {
    public static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);

    private static final String INDEX_NAME = "xunwu";
    private static final String INDEX_TYPE = "house";

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HouseDetailRepository houseDetailRepository;
    @Autowired
    private HouseTagRepository houseTagRepository;


    @Override
    public boolean index(Long houseId) {
        HouseIndexTemplate template = new HouseIndexTemplate();
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            LOGGER.error("Index House {} does not exist!", houseId);
            return false;
        }
        modelMapper.map(house, template);
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId);
        if (houseDetail == null) {
            LOGGER.error("houseDetail does not exist ,houseId:{}", houseId);
            return false;
        }
        modelMapper.map(houseDetail, template);
        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(houseId);
        if (!CollectionUtils.isEmpty(houseTags)) {
            List<String> collect = houseTags.stream().map(HouseTag::getName).collect(Collectors.toList());
            template.setTags(collect);
        }
        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
        LOGGER.debug(searchRequestBuilder.toString());

        boolean success;
        SearchResponse searchResponse = searchRequestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();
        if (totalHits == 0) {
            success = create(template);
        } else if (totalHits == 1) {
            String id = searchResponse.getHits().getAt(0).getId();
            success = update(id, template);
        } else {
            success = deleteAndCreate(totalHits, template);
        }
        return success;
    }

    @Override
    public boolean remove(Long houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for house:" + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        LOGGER.debug("Deleted total : ", deleted);
        return deleted > 0;
    }

    @Override
    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(
                QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName())
        );
        if (!StringUtils.isEmpty(rentSearch.getRegionEnName()) && !"*".equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName()));
        }
        //面积范围
        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlock.ALL.equals(area)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
            if (area.getMax() > 0) {
                rangeQuery.lte(area.getMax());
            }
            if (area.getMin() > 0) {
                rangeQuery.gte(area.getMin());
            }
            boolQuery.filter(rangeQuery);
        }
        //价格范围
        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlock.ALL.equals(price)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
            if (price.getMax() > 0) {
                rangeQuery.lte(price.getMax());
            }
            if (price.getMin() > 0) {
                rangeQuery.gte(price.getMin());
            }
            boolQuery.filter(rangeQuery);
        }
        //房屋朝向
        if (rentSearch.getDirection() > 0) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection()));
        }
        //出租方式
        if (rentSearch.getRentWay() > -1) {
            boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay()));
        }
        boolQuery.must(
                QueryBuilders.multiMatchQuery(
                        rentSearch.getKeywords(),
                        HouseIndexKey.TITLE,
                        HouseIndexKey.TRAFFIC,
                        HouseIndexKey.DISTRICT,
                        HouseIndexKey.ROUND_SERVICE,
                        HouseIndexKey.SUBWAY_LINE_NAME,
                        HouseIndexKey.SUBWAY_STATION_NAME
                )
        );

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        HouseSort.getSortKey(rentSearch.getOrderBy()),
                        SortOrder.fromString(rentSearch.getOrderDirection())
                )
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize());

        LOGGER.debug(searchRequestBuilder.toString());

        SearchResponse response = searchRequestBuilder.get();
        List<Long> houseIds = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            LOGGER.warn("search status is no ok for " + searchRequestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }
        for (SearchHit hit : response.getHits()) {
            houseIds.add(Long.parseLong(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
        }
        return new ServiceMultiResult<>(Math.toIntExact(response.getHits().getTotalHits()), houseIds);
    }

    private boolean create(HouseIndexTemplate houseIndexTemplate) {
        if (!updateSuggest(houseIndexTemplate)) {
            return false;
        }
        try {
            IndexResponse response = this.client.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON)
                    .get();
            LOGGER.debug("Create index with house:" + houseIndexTemplate.getHouseId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error to index house " + houseIndexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean update(String esId, HouseIndexTemplate houseIndexTemplate) {
        if (!updateSuggest(houseIndexTemplate)) {
            return false;
        }
        try {
            UpdateResponse response = this.client.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(houseIndexTemplate), XContentType.JSON)
                    .get();
            LOGGER.debug("Update index with house:" + houseIndexTemplate.getHouseId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error to index house " + houseIndexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate houseIndexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseIndexTemplate.getHouseId()))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for house:" + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            LOGGER.warn("Need deleted {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(houseIndexTemplate);
        }
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders
                .completionSuggestion("suggest")
                .prefix(prefix)
                .size(5);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autoCompletion", suggestion);

        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);
        LOGGER.debug(suggestBuilder.toString());

        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> autoCompletion = requestBuilder.get().getSuggest().getSuggestion("autoCompletion");
        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : autoCompletion.getEntries()) {
            if (entry instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) entry;
                if (item.getOptions().isEmpty()) {
                    continue;
                }
                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }
            if (maxSuggest > 5) {
                break;
            }
        }
        return ServiceResult.of(Lists.newArrayList(suggestSet.toArray(new String[]{})));
    }

    @Override
    public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, regionEnName))
                .filter(QueryBuilders.termQuery(HouseIndexKey.DISTRICT, district));

        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQueryBuilder)
                .addAggregation(
                        AggregationBuilders.terms(HouseIndexKey.AGG_DISTRICT)
                                .field(HouseIndexKey.DISTRICT)
                )
                .setSize(0);
        LOGGER.debug(requestBuilder.toString());
        SearchResponse response = requestBuilder.get();
        if (response.status() == RestStatus.OK) {
            Terms terms = response.getAggregations().get(HouseIndexKey.AGG_DISTRICT);
            if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
                return ServiceResult.of(terms.getBucketByKey(district).getDocCount());
            }
        } else {
            LOGGER.warn("Failed to Aggregate for " + HouseIndexKey.AGG_DISTRICT);
        }
        return ServiceResult.of(0L);
    }

    @Override
    public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION)
                .field(HouseIndexKey.REGION_EN_NAME);

        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQueryBuilder)
                .addAggregation(aggregationBuilder);

        LOGGER.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<HouseBucketDTO> bucketDTOList = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            LOGGER.warn("aggregate status is not ok for" + requestBuilder);
            return new ServiceMultiResult<>(0, bucketDTOList);
        } else {
            Terms terms = response.getAggregations().get(HouseIndexKey.AGG_REGION);
            for (Terms.Bucket bucket : terms.getBuckets()) {
                bucketDTOList.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
            }
        }
        return new ServiceMultiResult<>(Math.toIntExact(response.getHits().getTotalHits()), bucketDTOList);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, mapSearch.getCityEnName()));

        boolQuery.filter(
                QueryBuilders.geoBoundingBoxQuery("location")
                        .setCorners(
                                new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                                new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
                        )
        );

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(mapSearch.getOrderBy()), SortOrder.fromString(mapSearch.getOrderDirection()))
                .setFrom(mapSearch.getStart())
                .setSize(mapSearch.getSize());
        LOGGER.info(searchRequestBuilder.toString());

        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = searchRequestBuilder.get();
        if (RestStatus.OK != response.status()) {
            LOGGER.warn("Search status is not ok for " + searchRequestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }
        for (SearchHit hit : response.getHits()) {
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
        }
        return new ServiceMultiResult<>(Math.toIntExact(response.getHits().getTotalHits()), houseIds);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(String cityEnName,
                                             String orderBy,
                                             String orderDirection,
                                             int start,
                                             int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(orderBy), SortOrder.fromString(orderDirection))
                .setFrom(start)
                .setSize(size);

        LOGGER.info(searchRequestBuilder.toString());

        List<Long> houseIds = new ArrayList<>();

        SearchResponse response = searchRequestBuilder.get();
        if (response.status() != RestStatus.OK) {
            LOGGER.warn("Search status is not ok for " + searchRequestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }
        for (SearchHit hit : response.getHits()) {
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
        }
        return new ServiceMultiResult<>(Math.toIntExact(response.getHits().getTotalHits()), houseIds);
    }

    private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(this.client,
                AnalyzeAction.INSTANCE,
                INDEX_NAME,
                indexTemplate.getTitle(),
                indexTemplate.getLayoutDesc(),
                indexTemplate.getRoundService(),
                indexTemplate.getDescription(),
                indexTemplate.getTraffic(),
                indexTemplate.getSubwayLineName(),
                indexTemplate.getSubwayStationName());
        requestBuilder.setAnalyzer("ik_smart");

        AnalyzeResponse response = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            LOGGER.warn("Can not analyze token for house:" + indexTemplate.getHouseId());
            return false;
        }
        List<HouseSuggest> houseSuggestList = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            //排除数字类型 & 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }
            HouseSuggest suggest = new HouseSuggest();
            suggest.setInput(token.getTerm());
            houseSuggestList.add(suggest);
        }
        //定制化小区自动补全
        HouseSuggest suggest = new HouseSuggest();
        suggest.setInput(indexTemplate.getDistrict());
        houseSuggestList.add(suggest);

        indexTemplate.setSuggest(houseSuggestList);
        return true;
    }
}

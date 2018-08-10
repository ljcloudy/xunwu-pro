package com.cloudy.service.search;

import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.form.MapSearch;
import com.cloudy.web.form.RentSearch;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/7/28.
 */
public interface SearchService {

    boolean index(Long houseId);

    boolean remove(Long houseId);

    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /**
     * 推荐关键词
     * @param prefix
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);

    /**
     * 统计小区房源
     * @param cityEnName
     * @param regionEnName
     * @param district
     * @return
     */
    ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);

    /**
     * 地图聚合
     * @param cityEnName
     * @return
     */
    ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);

    ServiceMultiResult<Long> mapQuery(MapSearch mapSearch);

    ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size);
}

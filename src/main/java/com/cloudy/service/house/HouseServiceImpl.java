package com.cloudy.service.house;

import com.cloudy.base.HouseStatus;
import com.cloudy.base.LoginUserUtil;
import com.cloudy.entity.*;
import com.cloudy.repository.*;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.dto.HouseDTO;
import com.cloudy.web.dto.HouseDetailDTO;
import com.cloudy.web.dto.HousePictureDTO;
import com.cloudy.web.form.DatatableSearch;
import com.cloudy.web.form.HouseForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private SubwayRepository subwayRepository;
    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QiNiuService qiNiuService;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;


    @Override
    @Transactional
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperDetailInfo(detail, houseForm);

        if (subwayValidationResult != null) {
            return subwayValidationResult;
        }

        House house = new House();
        modelMapper.map(houseForm, house);

        Date now = new Date();

        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());

        houseRepository.save(house);

        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);

        List<HousePicture> pictureList = generatePictures(houseForm, house.getId());

        Iterable<HousePicture> housePictureIterable = housePictureRepository.save(pictureList);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> housePictureDTOList = new ArrayList<>();

        housePictureIterable.forEach(housePicture -> housePictureDTOList.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(housePictureDTOList);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (!CollectionUtils.isEmpty(tags)) {
            List<HouseTag> houseTagList = tags.stream().map(tag ->
                    new HouseTag(house.getId(), tag)
            ).collect(Collectors.toList());

            houseTagRepository.save(houseTagList);
            houseDTO.setTags(tags);
        }

        return new ServiceResult<>(true, null, houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult<HouseDTO> update(HouseForm houseForm) {

        House house = houseRepository.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFount();
        }
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(house.getId());
        if (houseDetail == null) {
            return ServiceResult.notFount();
        }

        ServiceResult<HouseDTO> wrapperDetailInfo = this.wrapperDetailInfo(houseDetail, houseForm);
        if (wrapperDetailInfo != null) {
            return wrapperDetailInfo;
        }
        houseDetailRepository.save(houseDetail);

        List<HousePicture> housePictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.save(housePictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }
        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);
        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
        List<HouseDTO> houseDTOS = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();

        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);

        Specification<House> specification = (root, Query, cb) -> {
            Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
            cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));

            if (!StringUtils.isEmpty(searchBody.getCity())) {
                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
            }

            if (searchBody.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
            }
            if (searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
            }
            if (searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
            }
            if (!StringUtils.isEmpty(searchBody.getTitle())) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
            }

            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<>(houses.getTotalPages(), houseDTOS);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFount();
        }

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(id);
        List<HousePicture> housePictures = housePictureRepository.findAllByHouseId(id);

        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(id);

        if (!CollectionUtils.isEmpty(houseTags)) {
            List<String> stringList = houseTags.stream()
                    .map(houseTag -> houseTag.getName())
                    .collect(Collectors.toList());
            houseDTO.setTags(stringList);
        }

        if (!CollectionUtils.isEmpty(housePictures)) {
            List<HousePictureDTO> housePictureDTOS = housePictures.stream()
                    .map(housePicture -> modelMapper.map(housePicture, HousePictureDTO.class))
                    .collect(Collectors.toList());
            houseDTO.setPictures(housePictureDTOS);
        }

        return ServiceResult.of(houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult removePhoto(Long id) {
        HousePicture housePicture = housePictureRepository.findOne(id);
        if (housePicture == null) {
            return ServiceResult.notFount();
        }
        try {
            Response response = qiNiuService.delete(housePicture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(id);
                return ServiceResult.success();
            } else {
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            return new ServiceResult(false, e.getMessage());
        }

    }

    @Override
    @Transactional
    public ServiceResult updateCover(Long coverId, Long targetId) {
        HousePicture housePicture = housePictureRepository.findOne(coverId);
        if (housePicture == null) {
            return ServiceResult.notFount();
        }
        housePictureRepository.updateCover(targetId, housePicture.getPath());

        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFount();
        }

        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }

        houseTagRepository.save(new HouseTag(houseId, tag));
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFount();
        }

        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ServiceResult(false, "标签不存在");
        }

        houseTagRepository.delete(houseTag.getId());
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult updateStatus(Long id, int status) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFount();
        }
        if (house.getStatus() == status) {
            return new ServiceResult(false, "状态没有发生改变！");
        }
        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ServiceResult(false, "已出租的房源不允许修改状态！");
        }
        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ServiceResult(false, "已删除的资源不循序操作！");
        }
        houseRepository.updateStatus(id, status);
        return ServiceResult.success();
    }

    private List<HousePicture> generatePictures(HouseForm houseForm, Long id) {
        List<HousePicture> housePictures = new ArrayList<>();
        if (houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()) {
            return housePictures;
        }

        housePictures = houseForm.getPhotos().stream().map(photoForm -> {
            HousePicture picture = new HousePicture();
            picture.setCdnPrefix(cdnPrefix);
            picture.setHouseId(id);
            picture.setHeight(photoForm.getHeight());
            picture.setWidth(photoForm.getWidth());
            picture.setPath(photoForm.getPath());
            return picture;
        }).collect(Collectors.toList());

        return housePictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail detail, HouseForm houseForm) {
        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());

        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line!");
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || subway.getId() != subwayStation.getSubwayId()) {
            return new ServiceResult<>(false, "Not valid subway station!");
        }

        detail.setSubwayLineId(subway.getId());
        detail.setSubwayLineName(subway.getName());

        detail.setSubwayStationId(subwayStation.getId());
        detail.setSubwayStationName(subwayStation.getName());

        detail.setDescription(houseForm.getDescription());
        detail.setDetailAddress(houseForm.getDetailAddress());
        detail.setLayoutDesc(houseForm.getLayoutDesc());
        detail.setRentWay(houseForm.getRentWay());
        detail.setRoundService(houseForm.getRoundService());
        detail.setTraffic(houseForm.getTraffic());

        return null;
    }
}

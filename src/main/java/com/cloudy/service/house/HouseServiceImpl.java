package com.cloudy.service.house;

import com.cloudy.base.LoginUserUtil;
import com.cloudy.entity.*;
import com.cloudy.repository.*;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.dto.HouseDTO;
import com.cloudy.web.dto.HouseDetailDTO;
import com.cloudy.web.dto.HousePictureDTO;
import com.cloudy.web.form.HouseForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;


    @Override
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

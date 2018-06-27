package com.cloudy.service.house;

import com.cloudy.entity.Subway;
import com.cloudy.entity.SubwayStation;
import com.cloudy.entity.SupportAddress;
import com.cloudy.repository.SubwayRepository;
import com.cloudy.repository.SubwayStationRepository;
import com.cloudy.repository.SupportAddressRepository;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.web.dto.SubwayDTO;
import com.cloudy.web.dto.SubwayStationDTO;
import com.cloudy.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {

        List<SupportAddress> supportAddressList = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());

        List<SupportAddressDTO> supportAddressDTOS = supportAddressList.stream()
                .map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(supportAddressDTOS.size(), supportAddressDTOS);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
        if (StringUtils.isEmpty(cityName)) {
            return new ServiceMultiResult<>(0, null);
        }
        List<SupportAddressDTO> result = new ArrayList<>();

        List<SupportAddress> supportAddressList = supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityName);
        if (!CollectionUtils.isEmpty(supportAddressList)) {
            List<SupportAddressDTO> dtoList = supportAddressList.stream()
                    .map(supportAddress -> (modelMapper.map(supportAddress, SupportAddressDTO.class)))
                    .collect(Collectors.toList());
            result = dtoList;
        }
        return new ServiceMultiResult<>(supportAddressList.size(), result);
    }

    @Override
    public ServiceMultiResult<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        if (StringUtils.isEmpty(cityEnName)) {
            return new ServiceMultiResult<>(0, null);
        }
        List<Subway> subwayList = subwayRepository.findAllByCityEnName(cityEnName);

        if (!CollectionUtils.isEmpty(subwayList)) {
            List<SubwayDTO> subwayDTOList = subwayList.stream()
                    .map(subway -> modelMapper.map(subway, SubwayDTO.class))
                    .collect(Collectors.toList());
            return new ServiceMultiResult<>(subwayDTOList.size(), subwayDTOList);
        }

        return new ServiceMultiResult<>(0, null);
    }

    @Override
    public ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId) {
        ServiceMultiResult<SubwayStationDTO> result = new ServiceMultiResult<>(0, null);

        if (subwayId == null || subwayId < 1) {
            return result;
        }

        List<SubwayStation> subwayStationList = subwayStationRepository.findAllBySubwayId(subwayId);

        if (!CollectionUtils.isEmpty(subwayStationList)) {
            List<SubwayStationDTO> subwayStationDTOList = subwayStationList.stream()
                    .map(subwayStation -> modelMapper.map(subwayStation, SubwayStationDTO.class))
                    .collect(Collectors.toList());
            result.setResult(subwayStationDTOList);
            result.setTotal(subwayStationDTOList.size());
            return result;
        }

        return result;
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> map = new HashMap<>();

        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());

        SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, cityEnName);

        map.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        map.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return map;
    }
}

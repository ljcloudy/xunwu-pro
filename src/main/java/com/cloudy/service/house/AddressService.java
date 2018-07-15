package com.cloudy.service.house;


import com.cloudy.entity.SupportAddress;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.ServiceResult;
import com.cloudy.web.dto.SubwayDTO;
import com.cloudy.web.dto.SubwayStationDTO;
import com.cloudy.web.dto.SupportAddressDTO;

import java.util.Map;


/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public interface AddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();


    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName);

    ServiceMultiResult<SubwayDTO> findAllSubwayByCity(String cityEnName);

    ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subway_id);

    Map<SupportAddress.Level,SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    ServiceResult<SubwayDTO> findSubway(Long subwayLineId);

    ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);
}

package com.cloudy.service.house;


import com.cloudy.service.ServiceMultiResult;
import com.cloudy.web.dto.SubwayDTO;
import com.cloudy.web.dto.SubwayStationDTO;
import com.cloudy.web.dto.SupportAddressDTO;



/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public interface AddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();


    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName);

    ServiceMultiResult<SubwayDTO> findAllSubwayByCity(String cityEnName);

    ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subway_id);
}

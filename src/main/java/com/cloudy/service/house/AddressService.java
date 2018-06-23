package com.cloudy.service.house;


import com.cloudy.service.ServiceMultiResult;
import com.cloudy.web.controller.house.SupportAddressDTO;



/**
 * Created by ljy_cloudy on 2018/6/10.
 */
public interface AddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();
}

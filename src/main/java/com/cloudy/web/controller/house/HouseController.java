package com.cloudy.web.controller.house;

import com.cloudy.base.ApiResponse;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.house.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ljy_cloudy on 2018/6/10.
 */
@Controller
public class HouseController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if(result.getResultSize() ==0){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(result.getResult());
    }
}

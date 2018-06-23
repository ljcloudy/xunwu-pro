package com.cloudy.web.controller.house;

import com.cloudy.base.ApiResponse;
import com.cloudy.service.ServiceMultiResult;
import com.cloudy.service.house.AddressService;
import com.cloudy.web.dto.SubwayDTO;
import com.cloudy.web.dto.SubwayStationDTO;
import com.cloudy.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 获取对应城市支持区域列表
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name")String cityEnName){

        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(cityEnName);
        if(addressResult.getResult() == null  || addressResult.getTotal() < 1){
            ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(addressResult.getResult());
    }

    @GetMapping("/address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name")String cityEnName){
        ServiceMultiResult<SubwayDTO> subwayListResult = addressService.findAllSubwayByCity(cityEnName);
        if(subwayListResult.getResult() == null || subwayListResult.getTotal() < 1){
            ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(subwayListResult.getResult());
    }

    @GetMapping("/address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId){
        ServiceMultiResult<SubwayStationDTO> serviceMultiResult = addressService.findAllSubwayStationBySubwayId(subwayId);

        if(serviceMultiResult == null || serviceMultiResult.getResult() == null || serviceMultiResult.getTotal() <1){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return  ApiResponse.ofSuccess(serviceMultiResult.getResult());
    }
}

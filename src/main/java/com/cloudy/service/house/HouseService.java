package com.cloudy.service.house;

import com.cloudy.service.ServiceResult;
import com.cloudy.web.dto.HouseDTO;
import com.cloudy.web.form.HouseForm;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface HouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);
}

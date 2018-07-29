package com.cloudy.service;

import com.cloudy.entity.User;
import com.cloudy.web.dto.UserDTO;

/**
 * Created by ljy_cloudy on 2018/6/1.
 */
public interface UserService {
    User findByName(String name);

    ServiceResult<UserDTO> findById(Long adminId);
}

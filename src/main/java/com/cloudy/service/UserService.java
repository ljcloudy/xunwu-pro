package com.cloudy.service;

import com.cloudy.entity.User;

/**
 * Created by ljy_cloudy on 2018/6/1.
 */
public interface UserService {
    User findByName(String name);
}

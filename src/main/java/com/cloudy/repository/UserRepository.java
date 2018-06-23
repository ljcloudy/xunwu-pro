package com.cloudy.repository;

import com.cloudy.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by ljy_cloudy on 2018/5/30.
 */
public interface UserRepository extends CrudRepository<User,Long> {

    User findByName(String name);
}

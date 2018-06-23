package com.cloudy.repository;

import com.cloudy.entity.House;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface HouseRepository extends CrudRepository<House,Long> {
}

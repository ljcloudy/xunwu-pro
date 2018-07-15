package com.cloudy.repository;

import com.cloudy.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/24.
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
    List<HouseTag> findAllByHouseId(Long id);

    HouseTag findByNameAndHouseId(String tag, Long houseId);
}

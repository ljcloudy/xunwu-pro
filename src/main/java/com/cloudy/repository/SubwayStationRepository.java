package com.cloudy.repository;

import com.cloudy.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {

    List<SubwayStation> findAllBySubwayId(Long subwayId);
}

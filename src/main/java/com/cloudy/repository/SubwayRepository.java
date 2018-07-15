package com.cloudy.repository;

import com.cloudy.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface SubwayRepository  extends CrudRepository<Subway, Long> {
    /**
     * 根据城市查询地铁线路
     * @param cityEnName
     * @return
     */
    List<Subway> findAllByCityEnName(String cityEnName);

}

package com.cloudy.repository;

import com.cloudy.entity.HousePicture;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {
    List<HousePicture> findAllByHouseId(Long id);

    @Modifying
    @Query("update House as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id")Long id, @Param(value = "cover") String cover);
}

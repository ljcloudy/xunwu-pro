package com.cloudy.repository;

import com.cloudy.entity.House;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by ljy_cloudy on 2018/6/23.
 */
public interface HouseRepository extends PagingAndSortingRepository<House, Long>, JpaSpecificationExecutor<House> {
    @Modifying
    @Query("update House  as house set house.status = :status where house.id =:id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);
}

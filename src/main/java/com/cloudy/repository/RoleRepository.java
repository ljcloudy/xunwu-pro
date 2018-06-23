package com.cloudy.repository;

import com.cloudy.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 角色数据DAO
 * Created by ljy_cloudy on 2018/6/1.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findRoleByUserId(Long userId);
}

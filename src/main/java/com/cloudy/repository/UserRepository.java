package com.cloudy.repository;

import com.cloudy.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by ljy_cloudy on 2018/5/30.
 */
public interface UserRepository extends CrudRepository<User,Long> {

    User findByName(String name);

    @Modifying
    @Query("update User as user set user.name=:name where id=:id")
    void updateUserName(@Param(value = "id")Long id,@Param(value = "name")String name);

    @Modifying
    @Query("update User as user set user.email=:email where id=:id")
    void updateEmail(@Param(value = "id")Long id,@Param(value = "email")String email);


    @Modifying
    @Query("update User as user set user.password=:password where id=:id")
    void updatePassword(@Param(value = "id")Long id,@Param(value = "password")String password);
}

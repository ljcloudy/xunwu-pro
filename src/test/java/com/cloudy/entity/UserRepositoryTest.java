package com.cloudy.entity;

import com.cloudy.ApplicationTest;
import com.cloudy.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ljy_cloudy on 2018/5/30.
 */
public class UserRepositoryTest extends ApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne(){
        User user = userRepository.findOne(1L);

        Assert.assertEquals("lijianyun",user.getName());
    }
}

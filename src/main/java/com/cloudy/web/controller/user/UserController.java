package com.cloudy.web.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by ljy_cloudy on 2018/6/2.
 */
@Controller
public class UserController {
    @GetMapping("/user/login")
    public String loginPage(){
        return "user/login";
    }

    @GetMapping("/user/center")
    public String centerPage(){
        return "user/center";
    }



//    @GetMapping("/user/center")
//    public String centerPage2(){
//        return "user/center";
//    }
}

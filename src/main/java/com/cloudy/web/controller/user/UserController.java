package com.cloudy.web.controller.user;

import com.cloudy.base.ApiResponse;
import com.cloudy.base.LoginUserUtil;
import com.cloudy.service.ServiceResult;
import com.cloudy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ljy_cloudy on 2018/6/2.
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/user/center")
    public String centerPage() {
        return "user/center";
    }

    @PostMapping("api/user/info")
    @ResponseBody
    public ApiResponse updateUserInfo(@RequestParam(value = "profile") String profile,
                                      @RequestParam(value = "value") String value) {
        if (StringUtils.isEmpty(value)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }
        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "不支持邮箱格式");
        }
        ServiceResult result = userService.modifyUserProfile(profile, value);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

//    @GetMapping("/user/center")
//    public String centerPage2(){
//        return "user/center";
//    }
}

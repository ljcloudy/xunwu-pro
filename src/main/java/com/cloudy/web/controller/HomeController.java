package com.cloudy.web.controller;

import com.cloudy.base.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ljy_cloudy on 2018/5/31.
 */
@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "来苗苗");
        model.addAttribute("age", "24");
        return "index";
    }

    @GetMapping("/get")
    @ResponseBody
    public ApiResponse get() {
        return ApiResponse.ofSuccess("成功！");
    }

    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }

    @GetMapping("/403")
    public String accessError() {
        return "403";
    }

    @GetMapping("/500")
    public String internalError(){
        return "500";
    }
    @GetMapping("/logout")
    public String logoutPage(){
        return "logout";
    }
}

package com.cloudy.base;

import com.cloudy.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by ljy_cloudy on 2018/6/24.
 */
public class LoginUserUtil {

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}

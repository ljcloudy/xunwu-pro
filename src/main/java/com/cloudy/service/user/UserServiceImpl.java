package com.cloudy.service.user;

import com.cloudy.base.LoginUserUtil;
import com.cloudy.entity.Role;
import com.cloudy.entity.User;
import com.cloudy.repository.RoleRepository;
import com.cloudy.repository.UserRepository;
import com.cloudy.service.ServiceResult;
import com.cloudy.service.UserService;
import com.cloudy.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy_cloudy on 2018/6/1.
 */
@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Override
    public User findByName(String name) {
        User user = userRepository.findByName(name);
        if (user == null) {
            return null;
        }
        List<Role> roleList = roleRepository.findRoleByUserId(user.getId());

        if (CollectionUtils.isEmpty(roleList)) {
            throw new DisabledException("权限非法");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();

        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setGrantedAuthorityList(authorities);
        return user;
    }

    @Override
    public ServiceResult<UserDTO> findById(Long userId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return ServiceResult.notFound();
        }
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ServiceResult.of(userDTO);
    }

    @Override
    @Transactional
    public ServiceResult modifyUserProfile(String profile, String value) {
        ServiceResult result = new ServiceResult(true);

        Long userId = LoginUserUtil.getLoginUserId();

        if (StringUtils.isEmpty(value)) {
            result.setSuccess(false);
            result.setMessage("属性不能为空！");
            return result;
        }
        switch (profile) {
            case "name":
                userRepository.updateUserName(userId, value);
                break;
            case "email":
                userRepository.updateEmail(userId, value);
                break;
            case "password":
                userRepository.updatePassword(userId, this.passwordEncoder.encodePassword(value, userId));
                break;
            default:
                return new ServiceResult(false,"不支持的属性");

        }

        return result;
    }
}

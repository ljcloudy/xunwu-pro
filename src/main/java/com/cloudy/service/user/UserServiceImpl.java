package com.cloudy.service.user;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
}

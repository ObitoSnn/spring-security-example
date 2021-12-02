package com.obitosnn.config.security;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.obitosnn.module.system.entity.Role;
import com.obitosnn.module.system.entity.User;
import com.obitosnn.module.system.entity.UserRole;
import com.obitosnn.module.system.mapper.RoleMapper;
import com.obitosnn.module.system.mapper.UserMapper;
import com.obitosnn.module.system.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ObitoSnn
 */
@Slf4j
@Component("customUserDetailService")
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (ObjectUtil.isEmpty(user)) {
            throw new UsernameNotFoundException("用户名无效");
        }
        // TODO，根据实际情况添加相应权限
        List<String> roleIdList = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId()))
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
        List<String> roleCodeList;
        final String prefix = "ROLE_";
        if (ObjectUtil.isEmpty(roleIdList)) {
            roleCodeList = Collections.singletonList(prefix + "anon");
        } else {
            roleCodeList = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIdList))
                    .stream().peek(role -> {
                        role.setCode(prefix + role.getCode());
                    }).map(Role::getCode).collect(Collectors.toList());
        }
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleCodeList.toArray(new String[0]));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()).authorities(authorities).build();
    }
}

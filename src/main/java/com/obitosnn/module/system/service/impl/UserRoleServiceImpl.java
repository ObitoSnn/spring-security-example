package com.obitosnn.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obitosnn.module.system.entity.UserRole;
import com.obitosnn.module.system.mapper.UserRoleMapper;
import com.obitosnn.module.system.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author ObitoSnn
 * @description 针对表【tbl_user_role(用户角色关联表)】的数据库操作Service实现
 * @createDate 2021-11-25 23:20:18
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
        implements UserRoleService {

}





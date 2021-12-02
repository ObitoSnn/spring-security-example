package com.obitosnn.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obitosnn.module.system.entity.Role;
import com.obitosnn.module.system.mapper.RoleMapper;
import com.obitosnn.module.system.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @author ObitoSnn
 * @description 针对表【tbl_role(角色表)】的数据库操作Service实现
 * @createDate 2021-11-25 23:20:18
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

}





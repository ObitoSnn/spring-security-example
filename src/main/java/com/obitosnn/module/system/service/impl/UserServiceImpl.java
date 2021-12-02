package com.obitosnn.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obitosnn.module.system.entity.User;
import com.obitosnn.module.system.mapper.UserMapper;
import com.obitosnn.module.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author ObitoSnn
 * @description 针对表【tbl_user(用户表)】的数据库操作Service实现
 * @createDate 2021-11-25 23:20:18
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

}





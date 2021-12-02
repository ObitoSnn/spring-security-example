package com.obitosnn.module.system.controller;

import com.obitosnn.module.system.entity.User;
import com.obitosnn.module.system.service.UserService;
import com.obitosnn.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ObitoSnn
 */
@Api(tags = "用户")
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("添加")
    @PostMapping("/add")
    public Result<?> add(@RequestBody User user) {
        return userService.save(user) ? Result.ok("添加成功") : Result.error("添加失败");
    }
}

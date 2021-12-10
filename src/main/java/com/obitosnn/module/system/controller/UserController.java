package com.obitosnn.module.system.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.obitosnn.module.system.entity.User;
import com.obitosnn.module.system.service.UserService;
import com.obitosnn.util.BcryptUtil;
import com.obitosnn.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author ObitoSnn
 */
@Api(tags = "用户")
@Controller
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("列表")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(userService.list());
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "body"),
                    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "body"),
                    @ApiImplicitParam(name = "password", value = "用户密码", required = true, paramType = "body")
            }
    )
    @ApiOperation(value = "修改")
    @PutMapping("/update")
    public Result<?> update(@ApiIgnore @RequestBody JSONObject request) {
        if (ObjectUtil.isEmpty(request.get("username")) || ObjectUtil.isEmpty(request.get("password"))) {
            return Result.error("用户名或密码不能为空");
        }
        User user = request.toJavaObject(User.class);
        user.setPassword(BcryptUtil.encrypt(user.getPassword()));
        return userService.updateById(user) ? Result.ok() : Result.error();
    }

    @ApiImplicitParam(name = "ids", value = "用户id，多个一,分开", paramType = "query")
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public Result<?> delete(String ids) {
        if (StrUtil.isBlank(ids)) {
            Result.error("参数ids不能为空");
        }
        Collection<String> idList = Arrays.asList(ids.split(","));
        userService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation("添加")
    @PostMapping("/add")
    public Result<?> add(@RequestBody User user) {
        if (ObjectUtil.isEmpty(user.getUsername()) || ObjectUtil.isEmpty(user.getPassword())) {
            return Result.error("用户名或密码不能为空");
        }
        user.setPassword(BcryptUtil.encrypt(user.getPassword()));
        return userService.save(user) ? Result.ok("添加成功") : Result.error("添加失败");
    }
}

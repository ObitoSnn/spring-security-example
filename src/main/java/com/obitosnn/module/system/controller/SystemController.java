package com.obitosnn.module.system.controller;

import com.obitosnn.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ObitoSnn
 */
@Api
@Controller
public class SystemController {

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "username", value = "用户名"),
                    @ApiImplicitParam(name = "password", value = "密码")
            }
    )
    @ApiOperation(value = "登录", tags = "登录")
    @PostMapping("/login")
    @ResponseBody
    public Result<?> login() {
        // 仅为了提供swagger接口文档
        return null;
    }

    @ApiOperation(value = "登出", tags = "登出")
    @PostMapping("/logout")
    public Result<?> logout() {
        // 仅为了提供swagger接口文档
        return null;
    }
}

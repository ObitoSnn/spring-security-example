package com.obitosnn.module.test.controller;

import com.obitosnn.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ObitoSnn
 */
@Api(tags = "测试")
@RestController
public class TestController {
    @ApiOperation(value = "测试接口，无admin角色不能访问")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_admin')")
    public Result<?> admin() {
        return Result.ok("admin");
    }

    @ApiOperation(value = "测试接口，无demo角色不能访问")
    @GetMapping("/demo")
    @PreAuthorize("hasRole('ROLE_demo')")
    public Result<?> demo() {
        return Result.ok("demo");
    }
}

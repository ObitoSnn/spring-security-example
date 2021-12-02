package com.obitosnn.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表
 *
 * @TableName tbl_user
 */
@ApiModel
@TableName(value = "tbl_user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(hidden = true)
    @TableId
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

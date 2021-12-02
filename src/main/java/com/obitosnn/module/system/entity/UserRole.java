package com.obitosnn.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联表
 *
 * @TableName tbl_user_role
 */
@TableName(value = "tbl_user_role")
@Data
public class UserRole implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private String roleId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

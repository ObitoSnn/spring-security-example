package com.obitosnn.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色表
 *
 * @TableName tbl_role
 */
@TableName(value = "tbl_role")
@Data
public class Role implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 角色编码
     */
    private String code;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

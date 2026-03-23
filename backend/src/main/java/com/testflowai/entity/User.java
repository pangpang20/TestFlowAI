package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表：t_user
 * @author TestFlowAI
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID (UUID)
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 (BCrypt 加密)
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：active-正常，disabled-禁用
     */
    private String status;

    /**
     * 最后登录时间
     */
    private Date lastLoginAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 删除时间
     */
    private Date deletedAt;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    private Integer deleted;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 更新者
     */
    private String updatedBy;

    /**
     * 删除者
     */
    private String deletedBy;
}

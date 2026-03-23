package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色实体类
 * 对应数据库表：t_role
 * @author TestFlowAI
 */
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色 ID (UUID)
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 描述
     */
    private String description;

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

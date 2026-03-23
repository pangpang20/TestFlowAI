package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 权限实体类
 * 对应数据库表：t_permission
 * @author TestFlowAI
 */
@Data
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限 ID (UUID)
     */
    private String permissionId;

    /**
     * 权限代码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 资源类型
     */
    private String resource;

    /**
     * 操作类型
     */
    private String action;

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

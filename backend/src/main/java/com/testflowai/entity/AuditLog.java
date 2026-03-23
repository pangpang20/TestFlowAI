package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 审计日志实体类
 * 对应数据库表：t_audit_log
 * @author TestFlowAI
 */
@Data
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志 ID (UUID)
     */
    private String logId;

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 资源类型
     */
    private String resource;

    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 详情 (JSON)
     */
    private String details;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private Date timestamp;

    /**
     * 操作结果 (success/failure)
     */
    private String result;

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

package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务实体类
 * 对应数据库表：t_scheduled_task
 * @author TestFlowAI
 */
@Data
public class ScheduledTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务 ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 测试流 ID
     */
    private String testId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * Cron 表达式
     */
    private String cronExpression;

    /**
     * 任务状态 (active/inactive)
     */
    private String status;

    /**
     * 上次执行时间
     */
    private Date lastExecutedAt;

    /**
     * 下次执行时间
     */
    private Date nextExecutionAt;

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

package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 执行记录实体类
 * 对应数据库表：t_execution
 * @author TestFlowAI
 */
@Data
public class Execution implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行 ID
     */
    private String executionId;

    /**
     * 测试流 ID
     */
    private String testId;

    /**
     * 执行模式 (manual/auto/scheduled)
     */
    private String mode;

    /**
     * 执行状态 (pending/running/passed/failed/stopped)
     */
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 总步骤数
     */
    private Integer totalSteps;

    /**
     * 通过步骤数
     */
    private Integer passedSteps;

    /**
     * 失败步骤数
     */
    private Integer failedSteps;

    /**
     * 执行输入 JSON
     */
    private String input;

    /**
     * 执行输出 JSON
     */
    private String output;

    /**
     * 步骤结果 JSON
     */
    private String stepResults;

    /**
     * 截图路径 JSON
     */
    private String screenshots;

    /**
     * 循环上下文 JSON
     */
    private String loopContext;

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

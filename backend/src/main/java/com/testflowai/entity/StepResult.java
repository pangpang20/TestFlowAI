package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 步骤执行结果实体类
 * 对应数据库表：t_step_result
 * @author TestFlowAI
 */
@Data
public class StepResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结果 ID
     */
    private String resultId;

    /**
     * 执行 ID
     */
    private String executionId;

    /**
     * 步骤 ID
     */
    private Integer stepId;

    /**
     * 状态 (pending/running/passed/failed/skipped)
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
     * 错误信息
     */
    private String errorMessage;

    /**
     * 截图路径
     */
    private String screenshot;

    /**
     * 日志
     */
    private String log;

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

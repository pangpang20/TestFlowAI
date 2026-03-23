package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 测试报告实体类
 * 对应数据库表：t_report
 * @author TestFlowAI
 */
@Data
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告 ID
     */
    private String reportId;

    /**
     * 执行 ID
     */
    private String executionId;

    /**
     * 测试流 ID
     */
    private String testId;

    /**
     * 生成时间
     */
    private Date generatedAt;

    /**
     * 摘要 JSON
     */
    private String summary;

    /**
     * 详情 JSON
     */
    private String details;

    /**
     * 对比数据 JSON
     */
    private String comparisons;

    /**
     * 格式 (html/json/pdf)
     */
    private String format;

    /**
     * 文件路径
     */
    private String filePath;

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

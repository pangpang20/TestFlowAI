package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 测试流实体类
 * 对应数据库表：t_testflow
 * @author TestFlowAI
 */
@Data
public class TestFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测试流 ID
     */
    private String testId;

    /**
     * 标题
     */
    private String title;

    /**
     * 版本号
     */
    private String version;

    /**
     * 应用 URL
     */
    private String appUrl;

    /**
     * 步骤 JSON
     */
    private String steps;

    /**
     * 变量 JSON
     */
    private String variables;

    /**
     * 标签 JSON
     */
    private String tags;

    /**
     * 预期报告 JSON
     */
    private String expectedReport;

    /**
     * 项目 ID
     */
    private String projectId;

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

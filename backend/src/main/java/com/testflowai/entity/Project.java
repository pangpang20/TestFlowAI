package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 项目实体类
 * 对应数据库表：t_project
 * @author TestFlowAI
 */
@Data
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目 ID (UUID)
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目负责人
     */
    private String owner;

    /**
     * 项目状态：active-进行中，completed-已完成，archived-已归档
     */
    private String status;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 进度 (0-100)
     */
    private Integer progress;

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

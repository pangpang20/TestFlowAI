package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.Project;
import com.testflowai.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 项目服务类
 * @author TestFlowAI
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectMapper projectMapper;

    public ProjectService(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    /**
     * 根据 ID 查询项目
     */
    public Project getById(String projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }
        return project;
    }

    /**
     * 获取所有项目列表
     */
    public List<Project> getAll() {
        return projectMapper.selectAll();
    }

    /**
     * 根据状态查询项目
     */
    public List<Project> getByStatus(String status) {
        return projectMapper.selectByStatus(status);
    }

    /**
     * 搜索项目
     */
    public List<Project> searchByName(String keyword) {
        return projectMapper.searchByName(keyword);
    }

    /**
     * 创建项目
     */
    @Transactional(rollbackFor = Exception.class)
    public Project create(Project project) {
        // 检查项目名称是否存在
        if (projectMapper.existsByName(project.getProjectName())) {
            throw new BusinessException("项目名称已存在");
        }

        // 设置默认状态
        if (project.getStatus() == null) {
            project.setStatus("active");
        }

        // 设置默认进度
        if (project.getProgress() == null) {
            project.setProgress(0);
        }

        // 设置 UUID
        project.setProjectId(UUID.randomUUID().toString());

        projectMapper.insert(project);
        logger.info("创建项目成功：{}", project.getProjectName());

        return project;
    }

    /**
     * 更新项目
     */
    @Transactional(rollbackFor = Exception.class)
    public Project update(String projectId, Project project) {
        // 检查项目是否存在
        Project existingProject = projectMapper.selectById(projectId);
        if (existingProject == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }

        // 如果修改了项目名称，检查新名称是否已存在
        if (project.getProjectName() != null && !project.getProjectName().equals(existingProject.getProjectName())) {
            if (projectMapper.existsByName(project.getProjectName())) {
                throw new BusinessException("项目名称已存在");
            }
        }

        projectMapper.update(project);
        logger.info("更新项目成功：{}", projectId);

        return project;
    }

    /**
     * 删除项目（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }

        projectMapper.deleteById(projectId);
        logger.info("删除项目成功：{}", projectId);
    }

    /**
     * 更新项目进度
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProgress(String projectId, Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new BusinessException("进度必须在 0-100 之间");
        }

        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }

        project.setProgress(progress);
        projectMapper.update(project);
        logger.info("更新项目进度成功：{} -> {}", projectId, progress);
    }

    /**
     * 更新项目状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String projectId, String status) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }

        project.setStatus(status);
        projectMapper.update(project);
        logger.info("更新项目状态成功：{} -> {}", projectId, status);
    }
}

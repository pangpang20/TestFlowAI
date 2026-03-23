package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.Project;
import com.testflowai.service.ProjectService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 项目控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 获取所有项目列表
     */
    @GetMapping
    public Result<List<Project>> getAll() {
        List<Project> projects = projectService.getAll();
        return Result.success(projects);
    }

    /**
     * 根据 ID 获取项目详情
     */
    @GetMapping("/{id}")
    public Result<Project> getById(@PathVariable String id) {
        Project project = projectService.getById(id);
        return Result.success(project);
    }

    /**
     * 根据状态查询项目
     */
    @GetMapping("/status/{status}")
    public Result<List<Project>> getByStatus(@PathVariable String status) {
        List<Project> projects = projectService.getByStatus(status);
        return Result.success(projects);
    }

    /**
     * 搜索项目
     */
    @GetMapping("/search")
    public Result<List<Project>> search(@RequestParam String keyword) {
        List<Project> projects = projectService.searchByName(keyword);
        return Result.success(projects);
    }

    /**
     * 创建项目
     */
    @PostMapping
    public Result<Project> create(@RequestBody CreateProjectRequest request) {
        Project project = new Project();
        project.setProjectName(request.projectName);
        project.setDescription(request.description);
        project.setOwner(request.owner);
        project.setStatus(request.status);
        project.setProgress(request.progress != null ? request.progress : 0);
        project.setStartDate(request.startDate);
        project.setEndDate(request.endDate);

        Project created = projectService.create(project);
        return Result.success("创建成功", created);
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable String id, @RequestBody UpdateProjectRequest request) {
        Project project = new Project();
        project.setProjectName(request.projectName);
        project.setDescription(request.description);
        project.setOwner(request.owner);
        project.setStatus(request.status);
        project.setProgress(request.progress);
        project.setStartDate(request.startDate);
        project.setEndDate(request.endDate);

        Project updated = projectService.update(id, project);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        projectService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 更新项目进度
     */
    @PatchMapping("/{id}/progress")
    public Result<Void> updateProgress(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request) {
        Integer progress = request.get("progress");
        projectService.updateProgress(id, progress);
        return Result.success("进度更新成功", null);
    }

    /**
     * 更新项目状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        projectService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    /**
     * 创建项目请求 DTO
     */
    public static class CreateProjectRequest {
        @NotBlank(message = "项目名称不能为空")
        private String projectName;

        private String description;
        private String owner;
        private String status;
        private Integer progress;
        private Date startDate;
        private Date endDate;

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
    }

    /**
     * 更新项目请求 DTO
     */
    public static class UpdateProjectRequest {
        private String projectName;
        private String description;
        private String owner;
        private String status;
        private Integer progress;
        private Date startDate;
        private Date endDate;

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
    }
}

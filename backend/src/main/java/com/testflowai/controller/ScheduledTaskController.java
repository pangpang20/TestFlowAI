package com.testflowai.controller;

import com.testflowai.common.BusinessException;
import com.testflowai.common.Result;
import com.testflowai.entity.ScheduledTask;
import com.testflowai.service.ScheduledTaskService;
import com.testflowai.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/scheduled-tasks")
public class ScheduledTaskController {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskController.class);

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建定时任务
     */
    @PostMapping
    public Result<ScheduledTask> createTask(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String taskName = request.get("taskName");
            String testId = request.get("testId");
            String cronExpression = request.get("cronExpression");
            String title = request.get("title");
            String description = request.get("description");
            String userId = getCurrentUserId(authorization);

            if (taskName == null || testId == null || cronExpression == null) {
                throw new BusinessException("缺少必填参数");
            }

            ScheduledTask task = scheduledTaskService.createTask(
                taskName, testId, cronExpression, title, description, userId);
            return Result.success(task);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建定时任务失败：{}", e.getMessage(), e);
            return Result.error("创建定时任务失败");
        }
    }

    /**
     * 更新定时任务
     */
    @PutMapping("/{taskId}")
    public Result<ScheduledTask> updateTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String taskName = request.get("taskName");
            String cronExpression = request.get("cronExpression");
            String title = request.get("title");
            String description = request.get("description");
            String userId = getCurrentUserId(authorization);

            ScheduledTask task = scheduledTaskService.updateTask(
                taskId, taskName, cronExpression, title, description, userId);
            return Result.success(task);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新定时任务失败：{}", e.getMessage(), e);
            return Result.error("更新定时任务失败");
        }
    }

    /**
     * 切换任务状态
     */
    @PatchMapping("/{taskId}/status")
    public Result<?> toggleTaskStatus(
            @PathVariable String taskId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String status = request.get("status");
            String userId = getCurrentUserId(authorization);

            if (status == null) {
                throw new BusinessException("状态不能为空");
            }

            scheduledTaskService.toggleTaskStatus(taskId, status, userId);
            return Result.success("状态更新成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新任务状态失败：{}", e.getMessage(), e);
            return Result.error("更新任务状态失败");
        }
    }

    /**
     * 手动触发任务
     */
    @PostMapping("/{taskId}/trigger")
    public Result<?> triggerTask(
            @PathVariable String taskId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            scheduledTaskService.triggerTask(taskId, userId);
            return Result.success("任务已触发");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("触发任务失败：{}", e.getMessage(), e);
            return Result.error("触发任务失败");
        }
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public Result<ScheduledTask> getTask(@PathVariable String taskId) {
        try {
            ScheduledTask task = scheduledTaskService.getTask(taskId);
            if (task == null) {
                return Result.error("任务不存在");
            }
            return Result.success(task);
        } catch (Exception e) {
            log.error("获取任务详情失败：{}", e.getMessage(), e);
            return Result.error("获取任务详情失败");
        }
    }

    /**
     * 获取任务列表
     */
    @GetMapping
    public Result<Map<String, Object>> getTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<ScheduledTask> tasks = scheduledTaskService.getTasks(page, size);
            long total = scheduledTaskService.getTotalTasks();

            Map<String, Object> result = new HashMap<>();
            result.put("list", tasks);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取任务列表失败：{}", e.getMessage(), e);
            return Result.error("获取任务列表失败");
        }
    }

    /**
     * 根据测试流 ID 获取任务列表
     */
    @GetMapping("/test/{testId}")
    public Result<List<ScheduledTask>> getTasksByTestId(@PathVariable String testId) {
        try {
            List<ScheduledTask> tasks = scheduledTaskService.getTasksByTestId(testId);
            return Result.success(tasks);
        } catch (Exception e) {
            log.error("获取任务列表失败：{}", e.getMessage(), e);
            return Result.error("获取任务列表失败");
        }
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{taskId}")
    public Result<?> deleteTask(
            @PathVariable String taskId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            scheduledTaskService.deleteTask(taskId, userId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除定时任务失败：{}", e.getMessage(), e);
            return Result.error("删除定时任务失败");
        }
    }

    /**
     * 获取当前用户 ID
     */
    private String getCurrentUserId(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                return jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                log.warn("解析 token 失败：{}", e.getMessage());
            }
        }
        return "system";
    }
}

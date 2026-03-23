package com.testflowai.service;

import com.testflowai.entity.Execution;
import com.testflowai.entity.ScheduledTask;
import com.testflowai.mapper.ScheduledTaskMapper;
import com.testflowai.common.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务服务
 * @author TestFlowAI
 */
@Service
public class ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    @Autowired
    private ExecutionService executionService;

    // 存储正在执行的任务
    private final Map<String, Boolean> runningTasks = new ConcurrentHashMap<>();

    /**
     * 创建定时任务
     */
    @Transactional
    public ScheduledTask createTask(String taskName, String testId, String cronExpression,
                                     String title, String description, String userId) {
        // 验证 Cron 表达式
        if (!isValidCronExpression(cronExpression)) {
            throw new BusinessException("无效的 Cron 表达式");
        }

        ScheduledTask task = new ScheduledTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskName(taskName);
        task.setTestId(testId);
        task.setTitle(title);
        task.setDescription(description);
        task.setCronExpression(cronExpression);
        task.setStatus("active");
        task.setNextExecutionAt(calculateNextExecution(cronExpression));
        task.setCreatedBy(userId);
        task.setUpdatedBy(userId);

        scheduledTaskMapper.insert(task);
        return task;
    }

    /**
     * 更新定时任务
     */
    @Transactional
    public ScheduledTask updateTask(String taskId, String taskName, String cronExpression,
                                     String title, String description, String userId) {
        ScheduledTask task = scheduledTaskMapper.getById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("定时任务不存在");
        }

        // 验证 Cron 表达式
        if (cronExpression != null && !isValidCronExpression(cronExpression)) {
            throw new BusinessException("无效的 Cron 表达式");
        }

        if (taskName != null) task.setTaskName(taskName);
        if (cronExpression != null) {
            task.setCronExpression(cronExpression);
            task.setNextExecutionAt(calculateNextExecution(cronExpression));
        }
        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        task.setUpdatedBy(userId);

        scheduledTaskMapper.update(task);
        return task;
    }

    /**
     * 激活/停用任务
     */
    @Transactional
    public void toggleTaskStatus(String taskId, String status, String userId) {
        ScheduledTask task = scheduledTaskMapper.getById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("定时任务不存在");
        }

        task.setStatus(status);
        task.setUpdatedBy(userId);
        scheduledTaskMapper.update(task);

        log.info("定时任务 {} 状态已更新为：{}", taskId, status);
    }

    /**
     * 手动触发任务
     */
    @Transactional
    public void triggerTask(String taskId, String userId) {
        ScheduledTask task = scheduledTaskMapper.getById(taskId);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("定时任务不存在");
        }

        if (!"active".equals(task.getStatus())) {
            throw new BusinessException("任务未激活，无法执行");
        }

        executeTask(task, userId);
    }

    /**
     * 删除定时任务
     */
    @Transactional
    public void deleteTask(String taskId, String userId) {
        ScheduledTask task = scheduledTaskMapper.getById(taskId);
        if (task == null) {
            throw new BusinessException("定时任务不存在");
        }

        scheduledTaskMapper.deleteById(taskId);
        log.info("定时任务 {} 已删除", taskId);
    }

    /**
     * 获取任务详情
     */
    public ScheduledTask getTask(String taskId) {
        return scheduledTaskMapper.getById(taskId);
    }

    /**
     * 获取任务列表
     */
    public List<ScheduledTask> getTasks(int page, int size) {
        int offset = (page - 1) * size;
        return scheduledTaskMapper.list(offset, size);
    }

    /**
     * 获取任务总数
     */
    public long getTotalTasks() {
        return scheduledTaskMapper.count();
    }

    /**
     * 根据测试流 ID 获取任务列表
     */
    public List<ScheduledTask> getTasksByTestId(String testId) {
        return scheduledTaskMapper.getByTestId(testId);
    }

    /**
     * 获取激活的任务列表
     */
    public List<ScheduledTask> getActiveTasks() {
        return scheduledTaskMapper.getByStatus("active");
    }

    /**
     * 定时检查并执行到期的任务
     */
    @Scheduled(fixedRate = 10000) // 每 10 秒检查一次
    public void checkAndExecuteTasks() {
        log.debug("检查到期的定时任务...");

        Date now = new Date();
        List<ScheduledTask> dueTasks = scheduledTaskMapper.getDueTasks(now);

        if (dueTasks.isEmpty()) {
            return;
        }

        log.info("发现 {} 个到期的定时任务", dueTasks.size());

        for (ScheduledTask task : dueTasks) {
            if (runningTasks.containsKey(task.getTaskId())) {
                log.warn("任务 {} 正在执行中，跳过", task.getTaskId());
                continue;
            }

            try {
                executeTask(task, "system");
            } catch (Exception e) {
                log.error("执行定时任务 {} 失败：{}", task.getTaskId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 执行单个任务
     */
    private void executeTask(ScheduledTask task, String userId) {
        runningTasks.put(task.getTaskId(), true);

        try {
            log.info("开始执行定时任务：{} (测试流：{})", task.getTaskId(), task.getTestId());

            // 创建执行记录
            Execution execution = executionService.createExecution(task.getTestId(), "scheduled", userId);

            // 异步执行测试流
            executionService.executeTestFlow(execution.getExecutionId(), userId)
                .thenAccept(result -> {
                    // 更新任务执行时间
                    Date now = new Date();
                    Date nextExec = calculateNextExecution(task.getCronExpression());

                    scheduledTaskMapper.updateExecutionTime(task.getTaskId(), now, nextExec);

                    log.info("定时任务 {} 执行完成，下次执行时间：{}", task.getTaskId(), nextExec);
                })
                .exceptionally(throwable -> {
                    log.error("执行任务 {} 失败：{}", task.getTaskId(), throwable.getMessage());
                    return null;
                });

        } finally {
            runningTasks.remove(task.getTaskId());
        }
    }

    /**
     * 验证 Cron 表达式
     */
    private boolean isValidCronExpression(String cronExpression) {
        try {
            // 简单的验证：检查是否有 5 个字段
            String[] parts = cronExpression.trim().split("\\s+");
            if (parts.length != 5) {
                return false;
            }

            // 验证每个字段的范围
            int[] ranges = {59, 23, 31, 12, 7}; // 分、时、日、月、周
            for (int i = 0; i < 5; i++) {
                String part = parts[i];
                if (part.equals("*")) continue;

                if (part.contains("-")) {
                    String[] range = part.split("-");
                    int min = Integer.parseInt(range[0]);
                    int max = Integer.parseInt(range[1]);
                    if (min < 0 || max > ranges[i] || min > max) return false;
                } else if (part.contains(",")) {
                    for (String p : part.split(",")) {
                        int val = Integer.parseInt(p.trim());
                        if (val < 0 || val > ranges[i]) return false;
                    }
                } else if (part.contains("/")) {
                    String[] rangeAndStep = part.split("/");
                    int step = Integer.parseInt(rangeAndStep[1]);
                    if (step <= 0) return false;
                } else {
                    int val = Integer.parseInt(part);
                    if (val < 0 || val > ranges[i]) return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算下次执行时间
     */
    private Date calculateNextExecution(String cronExpression) {
        // 简单实现：返回 1 分钟后的时间
        // 实际应该使用 CronUtils 或类似工具精确计算
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        return calendar.getTime();
    }
}

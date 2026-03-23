package com.testflowai.service;

import com.testflowai.entity.Execution;
import com.testflowai.entity.StepResult;
import com.testflowai.entity.TestFlow;
import com.testflowai.mapper.ExecutionMapper;
import com.testflowai.mapper.StepResultMapper;
import com.testflowai.mapper.TestFlowMapper;
import com.testflowai.common.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试执行服务
 * @author TestFlowAI
 */
@Service
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    @Autowired
    private ExecutionMapper executionMapper;

    @Autowired
    private StepResultMapper stepResultMapper;

    @Autowired
    private TestFlowMapper testFlowMapper;

    @Autowired(required = false)
    private PlaywrightService playwrightService;

    @Autowired
    private ExecutionLogService executionLogService;

    // 存储运行中的执行任务
    private final Map<String, ExecutionTask> runningTasks = new ConcurrentHashMap<>();

    /**
     * 创建执行记录
     */
    public Execution createExecution(String testId, String mode, String userId) {
        TestFlow testFlow = testFlowMapper.selectById(testId);
        if (testFlow == null || testFlow.getDeleted() == 1) {
            throw new BusinessException("测试流不存在或已被删除");
        }

        Execution execution = new Execution();
        execution.setExecutionId(UUID.randomUUID().toString());
        execution.setTestId(testId);
        execution.setMode(mode);
        execution.setStatus("pending");
        execution.setStartTime(new Date());
        execution.setTotalSteps(0);
        execution.setPassedSteps(0);
        execution.setFailedSteps(0);
        execution.setCreatedBy(userId);
        execution.setUpdatedBy(userId);

        executionMapper.insert(execution);
        return execution;
    }

    /**
     * 开始执行
     */
    public void startExecution(String executionId) {
        executionMapper.updateStatus(executionId, "running");
    }

    /**
     * 更新执行状态
     */
    public void updateExecutionStatus(String executionId, String status, String userId) {
        Execution execution = executionMapper.getById(executionId);
        if (execution == null) {
            throw new BusinessException("执行记录不存在");
        }

        execution.setStatus(status);
        if ("passed".equals(status) || "failed".equals(status) || "stopped".equals(status)) {
            execution.setEndTime(new Date());
        }
        execution.setUpdatedBy(userId);
        executionMapper.update(execution);
    }

    /**
     * 更新执行统计
     */
    public void updateExecutionStats(String executionId, int totalSteps, int passedSteps, int failedSteps, String userId) {
        Execution execution = executionMapper.getById(executionId);
        if (execution == null) {
            throw new BusinessException("执行记录不存在");
        }

        execution.setTotalSteps(totalSteps);
        execution.setPassedSteps(passedSteps);
        execution.setFailedSteps(failedSteps);
        execution.setUpdatedBy(userId);
        executionMapper.update(execution);
    }

    /**
     * 保存步骤结果
     */
    @Transactional
    public void saveStepResult(StepResult stepResult) {
        stepResultMapper.insert(stepResult);
    }

    /**
     * 批量保存步骤结果
     */
    @Transactional
    public void batchSaveStepResults(List<StepResult> stepResults) {
        if (stepResults != null && !stepResults.isEmpty()) {
            stepResultMapper.batchInsert(stepResults);
        }
    }

    /**
     * 获取执行记录详情
     */
    public Execution getExecution(String executionId) {
        return executionMapper.getById(executionId);
    }

    /**
     * 获取测试流的执行历史
     */
    public List<Execution> getExecutionHistory(String testId) {
        return executionMapper.getByTestId(testId);
    }

    /**
     * 获取执行列表（分页）
     */
    public List<Execution> getExecutions(int page, int size) {
        int offset = (page - 1) * size;
        return executionMapper.list(offset, size);
    }

    /**
     * 获取执行总数
     */
    public long getTotalExecutions() {
        return executionMapper.count();
    }

    /**
     * 获取状态统计
     */
    public Map<String, Long> getStatusCount() {
        List<ExecutionMapper.ExecutionStatusCount> counts = executionMapper.countByStatus();
        Map<String, Long> result = new HashMap<>();
        for (ExecutionMapper.ExecutionStatusCount count : counts) {
            result.put(count.getStatus(), count.getCount());
        }
        return result;
    }

    /**
     * 获取最近的执行记录
     */
    public List<Execution> getRecentExecutions(int limit) {
        return executionMapper.getRecentExecutions(limit);
    }

    /**
     * 获取执行对应的步骤结果
     */
    public List<StepResult> getStepResults(String executionId) {
        return stepResultMapper.getByExecutionId(executionId);
    }

    /**
     * 删除执行记录
     */
    @Transactional
    public void deleteExecution(String executionId, String userId) {
        Execution execution = executionMapper.getById(executionId);
        if (execution == null) {
            throw new BusinessException("执行记录不存在");
        }

        // 删除关联的步骤结果
        stepResultMapper.deleteByExecutionId(executionId);
        // 删除执行记录
        executionMapper.deleteById(executionId);
    }

    /**
     * 停止执行中的任务
     */
    public void stopExecution(String executionId, String userId) {
        ExecutionTask task = runningTasks.get(executionId);
        if (task != null) {
            task.cancel();
            updateExecutionStatus(executionId, "stopped", userId);
            runningTasks.remove(executionId);
        }
    }

    /**
     * 执行测试流（模拟执行）
     */
    public CompletableFuture<Execution> executeTestFlow(String executionId, String userId) {
        ExecutionTask task = new ExecutionTask(executionId, userId);
        runningTasks.put(executionId, task);
        return task.execute();
    }

    /**
     * 内部类：执行任务
     */
    private class ExecutionTask {
        private final String executionId;
        private final String userId;
        private volatile boolean cancelled = false;

        public ExecutionTask(String executionId, String userId) {
            this.executionId = executionId;
            this.userId = userId;
        }

        public CompletableFuture<Execution> execute() {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    // 获取测试流
                    Execution execution = executionMapper.getById(executionId);
                    if (execution == null) {
                        throw new BusinessException("执行记录不存在");
                    }

                    TestFlow testFlow = testFlowMapper.selectById(execution.getTestId());
                    if (testFlow == null) {
                        throw new BusinessException("测试流不存在");
                    }

                    // 解析步骤
                    List<Map<String, Object>> steps = parseSteps(testFlow.getSteps());
                    int totalSteps = steps.size();

                    // 启动浏览器
                    if (playwrightService != null) {
                        playwrightService.launchBrowser();
                    }

                    // 开始执行
                    startExecution(executionId);
                    executionLogService.pushLog(executionId, "开始执行测试：" + testFlow.getTitle());

                    List<StepResult> stepResults = new ArrayList<>();
                    int passedSteps = 0;
                    int failedSteps = 0;

                    for (int i = 0; i < steps.size() && !cancelled; i++) {
                        Map<String, Object> step = steps.get(i);

                        // 推送步骤开始消息
                        String description = (String) step.get("description");
                        executionLogService.pushStepStart(executionId, i + 1, description);

                        StepResult stepResult = executeStep(executionId, i + 1, step);
                        stepResults.add(stepResult);

                        // 推送步骤结束消息
                        executionLogService.pushStepEnd(executionId, i + 1, stepResult.getStatus(), stepResult.getErrorMessage());

                        if ("passed".equals(stepResult.getStatus())) {
                            passedSteps++;
                        } else if ("failed".equals(stepResult.getStatus())) {
                            failedSteps++;
                        }
                    }

                    // 批量保存步骤结果
                    batchSaveStepResults(stepResults);

                    // 更新执行统计
                    updateExecutionStats(executionId, totalSteps, passedSteps, failedSteps, userId);

                    // 确定最终状态
                    String finalStatus = cancelled ? "stopped" : (failedSteps > 0 ? "failed" : "passed");
                    updateExecutionStatus(executionId, finalStatus, userId);

                    // 推送执行完成消息
                    executionLogService.pushComplete(executionId, finalStatus, totalSteps, passedSteps, failedSteps);

                    return executionMapper.getById(executionId);

                } catch (Exception e) {
                    log.error("执行测试流失败：{}", e.getMessage(), e);
                    executionLogService.pushError(executionId, e.getMessage());
                    updateExecutionStatus(executionId, "failed", userId);
                    throw new BusinessException("执行失败：" + e.getMessage());
                } finally {
                    // 关闭浏览器
                    if (playwrightService != null) {
                        playwrightService.closeBrowser();
                    }
                    runningTasks.remove(executionId);
                }
            });
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> parseSteps(String stepsJson) {
            if (stepsJson == null || stepsJson.isEmpty()) {
                return new ArrayList<>();
            }
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(stepsJson, List.class);
            } catch (Exception e) {
                log.error("解析步骤 JSON 失败：{}", e.getMessage());
                return new ArrayList<>();
            }
        }

        private StepResult executeStep(String executionId, int stepId, Map<String, Object> step) {
            StepResult stepResult = new StepResult();
            stepResult.setResultId(UUID.randomUUID().toString());
            stepResult.setExecutionId(executionId);
            stepResult.setStepId(stepId);
            stepResult.setStartTime(new Date());

            try {
                String type = (String) step.get("type");
                String description = (String) step.get("description");
                String selector = (String) step.get("selector");

                // 模拟执行步骤（实际应该调用 Playwright）
                log.info("执行步骤 {}: {} - {}", stepId, type, description);

                // 模拟执行延迟
                Thread.sleep(100);

                stepResult.setStatus("passed");
                stepResult.setLog(String.format("步骤 %d 执行成功：%s (%s)", stepId, type, description));

            } catch (InterruptedException e) {
                stepResult.setStatus("stopped");
                stepResult.setErrorMessage("步骤被中断");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                stepResult.setStatus("failed");
                stepResult.setErrorMessage(e.getMessage());
                log.error("步骤执行失败：{}", e.getMessage());
            }

            stepResult.setEndTime(new Date());
            stepResult.setCreatedBy(userId);
            stepResult.setUpdatedBy(userId);

            return stepResult;
        }

        public void cancel() {
            this.cancelled = true;
        }
    }
}

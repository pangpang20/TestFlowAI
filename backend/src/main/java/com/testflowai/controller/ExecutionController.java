package com.testflowai.controller;

import com.testflowai.common.BusinessException;
import com.testflowai.common.Result;
import com.testflowai.entity.Execution;
import com.testflowai.entity.StepResult;
import com.testflowai.service.ExecutionService;
import com.testflowai.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 测试执行控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "*")
public class ExecutionController {

    private static final Logger log = LoggerFactory.getLogger(ExecutionController.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建执行记录
     */
    @PostMapping
    public Result<Execution> createExecution(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String testId = request.get("testId");
            String mode = request.getOrDefault("mode", "manual");
            String userId = getCurrentUserId(authorization);

            if (testId == null || testId.isEmpty()) {
                throw new BusinessException("测试流 ID 不能为空");
            }

            Execution execution = executionService.createExecution(testId, mode, userId);
            return Result.success(execution);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建执行记录失败：{}", e.getMessage(), e);
            return Result.error("创建执行记录失败");
        }
    }

    /**
     * 开始执行
     */
    @PostMapping("/{executionId}/start")
    public Result<?> startExecution(
            @PathVariable String executionId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            executionService.startExecution(executionId);
            return Result.success("执行已开始");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("开始执行失败：{}", e.getMessage(), e);
            return Result.error("开始执行失败");
        }
    }

    /**
     * 执行测试流
     */
    @PostMapping("/{executionId}/run")
    public Result<Execution> runTestFlow(
            @PathVariable String executionId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);

            // 异步执行测试流
            CompletableFuture<Execution> future = executionService.executeTestFlow(executionId, userId);

            Execution execution = future.get();
            return Result.success(execution);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("执行测试流失败：{}", e.getMessage(), e);
            return Result.error("执行测试流失败：" + e.getMessage());
        }
    }

    /**
     * 停止执行
     */
    @PostMapping("/{executionId}/stop")
    public Result<?> stopExecution(
            @PathVariable String executionId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            executionService.stopExecution(executionId, userId);
            return Result.success("执行已停止");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("停止执行失败：{}", e.getMessage(), e);
            return Result.error("停止执行失败");
        }
    }

    /**
     * 获取执行记录详情
     */
    @GetMapping("/{executionId}")
    public Result<Execution> getExecution(@PathVariable String executionId) {
        try {
            Execution execution = executionService.getExecution(executionId);
            if (execution == null) {
                return Result.error("执行记录不存在");
            }
            return Result.success(execution);
        } catch (Exception e) {
            log.error("获取执行记录失败：{}", e.getMessage(), e);
            return Result.error("获取执行记录失败");
        }
    }

    /**
     * 获取执行历史（按测试流）
     */
    @GetMapping("/test/{testId}")
    public Result<List<Execution>> getExecutionHistory(@PathVariable String testId) {
        try {
            List<Execution> executions = executionService.getExecutionHistory(testId);
            return Result.success(executions);
        } catch (Exception e) {
            log.error("获取执行历史失败：{}", e.getMessage(), e);
            return Result.error("获取执行历史失败");
        }
    }

    /**
     * 获取执行列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getExecutions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Execution> executions = executionService.getExecutions(page, size);
            long total = executionService.getTotalExecutions();

            Map<String, Object> result = new HashMap<>();
            result.put("list", executions);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取执行列表失败：{}", e.getMessage(), e);
            return Result.error("获取执行列表失败");
        }
    }

    /**
     * 获取状态统计
     */
    @GetMapping("/stats/status")
    public Result<Map<String, Long>> getStatusCount() {
        try {
            Map<String, Long> stats = executionService.getStatusCount();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取状态统计失败：{}", e.getMessage(), e);
            return Result.error("获取状态统计失败");
        }
    }

    /**
     * 获取最近的执行记录
     */
    @GetMapping("/recent")
    public Result<List<Execution>> getRecentExecutions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Execution> executions = executionService.getRecentExecutions(limit);
            return Result.success(executions);
        } catch (Exception e) {
            log.error("获取最近执行记录失败：{}", e.getMessage(), e);
            return Result.error("获取最近执行记录失败");
        }
    }

    /**
     * 获取步骤结果
     */
    @GetMapping("/{executionId}/steps")
    public Result<List<StepResult>> getStepResults(@PathVariable String executionId) {
        try {
            List<StepResult> stepResults = executionService.getStepResults(executionId);
            return Result.success(stepResults);
        } catch (Exception e) {
            log.error("获取步骤结果失败：{}", e.getMessage(), e);
            return Result.error("获取步骤结果失败");
        }
    }

    /**
     * 删除执行记录
     */
    @DeleteMapping("/{executionId}")
    public Result<?> deleteExecution(
            @PathVariable String executionId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            executionService.deleteExecution(executionId, userId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除执行记录失败：{}", e.getMessage(), e);
            return Result.error("删除执行记录失败");
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

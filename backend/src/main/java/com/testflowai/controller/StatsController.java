package com.testflowai.controller;

import com.testflowai.entity.*;
import com.testflowai.mapper.*;
import com.testflowai.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 仪表盘统计接口
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private static final Logger log = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TestFlowMapper testFlowMapper;

    @Autowired
    private ExecutionMapper executionMapper;

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 项目统计
            long projectCount = projectMapper.selectAll().stream()
                .filter(p -> p.getDeleted() == null || p.getDeleted() == 0)
                .count();
            stats.put("projectCount", projectCount);

            // 测试用例统计
            long testFlowCount = testFlowMapper.selectAll().stream()
                .filter(t -> t.getDeleted() == null || t.getDeleted() == 0)
                .count();
            stats.put("testFlowCount", testFlowCount);

            // 执行统计
            Map<String, Long> statusCount = getStatusCount();
            stats.put("executionStatus", statusCount);

            // 计算通过率
            long passed = statusCount.getOrDefault("passed", 0L);
            long total = passed + statusCount.getOrDefault("failed", 0L) + statusCount.getOrDefault("stopped", 0L);
            double passRate = total > 0 ? (double) passed / total * 100 : 0;
            stats.put("passRate", Math.round(passRate * 100.0) / 100.0);

            // 定时任务统计
            long activeTasks = scheduledTaskMapper.getByStatus("active").size();
            stats.put("activeTasks", activeTasks);

            // 最近执行
            stats.put("recentExecutions", executionMapper.getRecentExecutions(5));

            return Result.success(stats);

        } catch (Exception e) {
            log.error("获取统计数据失败：{}", e.getMessage(), e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取执行状态统计
     */
    @GetMapping("/execution/status")
    public Result<Map<String, Long>> getExecutionStatusStats() {
        try {
            Map<String, Long> statusCount = getStatusCount();
            return Result.success(statusCount);
        } catch (Exception e) {
            log.error("获取执行状态统计失败：{}", e.getMessage(), e);
            return Result.error("获取执行状态统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近执行记录
     */
    @GetMapping("/execution/recent")
    public Result<List<Execution>> getRecentExecutions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Execution> executions = executionMapper.getRecentExecutions(limit);
            return Result.success(executions);
        } catch (Exception e) {
            log.error("获取最近执行记录失败：{}", e.getMessage(), e);
            return Result.error("获取最近执行记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取项目统计
     */
    @GetMapping("/projects")
    public Result<Map<String, Object>> getProjectStats() {
        try {
            List<Project> allProjects = projectMapper.selectAll();
            List<Project> activeProjects = new ArrayList<>();

            for (Project project : allProjects) {
                if (project.getDeleted() == null || project.getDeleted() == 0) {
                    activeProjects.add(project);
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", activeProjects.size());
            stats.put("list", activeProjects);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取项目统计失败：{}", e.getMessage(), e);
            return Result.error("获取项目统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取测试用例统计
     */
    @GetMapping("/testflows")
    public Result<Map<String, Object>> getTestFlowStats() {
        try {
            List<TestFlow> allTestFlows = testFlowMapper.selectAll();
            List<TestFlow> activeTestFlows = new ArrayList<>();

            for (TestFlow testFlow : allTestFlows) {
                if (testFlow.getDeleted() == null || testFlow.getDeleted() == 0) {
                    activeTestFlows.add(testFlow);
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", activeTestFlows.size());
            stats.put("list", activeTestFlows);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取测试用例统计失败：{}", e.getMessage(), e);
            return Result.error("获取测试用例统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取定时任务统计
     */
    @GetMapping("/scheduled-tasks")
    public Result<Map<String, Object>> getScheduledTaskStats() {
        try {
            long total = scheduledTaskMapper.count();
            long active = scheduledTaskMapper.getByStatus("active").size();
            long inactive = total - active;

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("active", active);
            stats.put("inactive", inactive);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取定时任务统计失败：{}", e.getMessage(), e);
            return Result.error("获取定时任务统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取执行状态统计（内部方法）
     */
    private Map<String, Long> getStatusCount() {
        List<ExecutionMapper.ExecutionStatusCount> counts = executionMapper.countByStatus();
        Map<String, Long> result = new HashMap<>();
        for (ExecutionMapper.ExecutionStatusCount count : counts) {
            result.put(count.getStatus(), count.getCount());
        }
        return result;
    }
}

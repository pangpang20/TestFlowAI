package com.testflowai.service;

import com.testflowai.entity.Execution;
import com.testflowai.entity.Report;
import com.testflowai.entity.StepResult;
import com.testflowai.entity.TestFlow;
import com.testflowai.mapper.ExecutionMapper;
import com.testflowai.mapper.ReportMapper;
import com.testflowai.mapper.StepResultMapper;
import com.testflowai.mapper.TestFlowMapper;
import com.testflowai.common.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 测试报告服务
 * @author TestFlowAI
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ExecutionMapper executionMapper;

    @Autowired
    private StepResultMapper stepResultMapper;

    @Autowired
    private TestFlowMapper testFlowMapper;

    /**
     * 生成报告
     */
    public Report generateReport(String executionId, String userId) {
        // 获取执行记录
        Execution execution = executionMapper.getById(executionId);
        if (execution == null) {
            throw new BusinessException("执行记录不存在");
        }

        // 获取测试流
        TestFlow testFlow = testFlowMapper.selectById(execution.getTestId());

        // 获取步骤结果
        List<StepResult> stepResults = stepResultMapper.getByExecutionId(executionId);

        // 生成报告摘要
        Map<String, Object> summary = buildSummary(execution, stepResults);

        // 生成报告详情
        Map<String, Object> details = buildDetails(execution, testFlow, stepResults);

        // 创建报告
        Report report = new Report();
        report.setReportId(UUID.randomUUID().toString());
        report.setExecutionId(executionId);
        report.setTestId(execution.getTestId());
        report.setGeneratedAt(new Date());
        report.setFormat("json");

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            report.setSummary(mapper.writeValueAsString(summary));
            report.setDetails(mapper.writeValueAsString(details));
        } catch (Exception e) {
            log.error("序列化报告数据失败：{}", e.getMessage());
            throw new BusinessException("生成报告失败");
        }

        report.setCreatedBy(userId);
        report.setUpdatedBy(userId);

        reportMapper.insert(report);
        return report;
    }

    /**
     * 构建报告摘要
     */
    private Map<String, Object> buildSummary(Execution execution, List<StepResult> stepResults) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("executionId", execution.getExecutionId());
        summary.put("testId", execution.getTestId());
        summary.put("status", execution.getStatus());
        summary.put("mode", execution.getMode());
        summary.put("startTime", execution.getStartTime());
        summary.put("endTime", execution.getEndTime());

        // 计算持续时间
        if (execution.getStartTime() != null && execution.getEndTime() != null) {
            long duration = execution.getEndTime().getTime() - execution.getStartTime().getTime();
            summary.put("duration", duration + "ms");
        }

        summary.put("totalSteps", execution.getTotalSteps());
        summary.put("passedSteps", execution.getPassedSteps());
        summary.put("failedSteps", execution.getFailedSteps());

        // 计算通过率
        if (execution.getTotalSteps() > 0) {
            double passRate = (double) execution.getPassedSteps() / execution.getTotalSteps() * 100;
            summary.put("passRate", String.format("%.2f%%", passRate));
        }

        return summary;
    }

    /**
     * 构建报告详情
     */
    private Map<String, Object> buildDetails(Execution execution, TestFlow testFlow, List<StepResult> stepResults) {
        Map<String, Object> details = new LinkedHashMap<>();

        // 测试流信息
        Map<String, Object> testInfo = new LinkedHashMap<>();
        testInfo.put("title", testFlow != null ? testFlow.getTitle() : "Unknown");
        testInfo.put("version", testFlow != null ? testFlow.getVersion() : "N/A");
        testInfo.put("appUrl", testFlow != null ? testFlow.getAppUrl() : "N/A");
        details.put("testInfo", testInfo);

        // 执行信息
        Map<String, Object> execInfo = new LinkedHashMap<>();
        execInfo.put("executionId", execution.getExecutionId());
        execInfo.put("mode", execution.getMode());
        execInfo.put("status", execution.getStatus());
        details.put("executionInfo", execInfo);

        // 步骤详情
        List<Map<String, Object>> stepDetails = new ArrayList<>();
        for (StepResult stepResult : stepResults) {
            Map<String, Object> stepDetail = new LinkedHashMap<>();
            stepDetail.put("stepId", stepResult.getStepId());
            stepDetail.put("status", stepResult.getStatus());
            stepDetail.put("startTime", stepResult.getStartTime());
            stepDetail.put("endTime", stepResult.getEndTime());
            stepDetail.put("errorMessage", stepResult.getErrorMessage());
            stepDetail.put("screenshot", stepResult.getScreenshot());
            stepDetail.put("log", stepResult.getLog());

            // 计算步骤执行时间
            if (stepResult.getStartTime() != null && stepResult.getEndTime() != null) {
                long stepDuration = stepResult.getEndTime().getTime() - stepResult.getStartTime().getTime();
                stepDetail.put("duration", stepDuration + "ms");
            }

            stepDetails.add(stepDetail);
        }
        details.put("stepDetails", stepDetails);

        return details;
    }

    /**
     * 获取报告
     */
    public Report getReport(String reportId) {
        return reportMapper.getById(reportId);
    }

    /**
     * 根据执行 ID 获取报告
     */
    public Report getReportByExecutionId(String executionId) {
        return reportMapper.getByExecutionId(executionId);
    }

    /**
     * 获取测试流的报告列表
     */
    public List<Report> getReportsByTestId(String testId) {
        return reportMapper.getByTestId(testId);
    }

    /**
     * 获取报告列表（分页）
     */
    public List<Report> getReports(int page, int size) {
        int offset = (page - 1) * size;
        return reportMapper.list(offset, size);
    }

    /**
     * 获取报告总数
     */
    public long getTotalReports() {
        return reportMapper.count();
    }

    /**
     * 删除报告
     */
    public void deleteReport(String reportId, String userId) {
        Report report = reportMapper.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        reportMapper.deleteById(reportId);
    }

    /**
     * 获取报告的 JSON 数据
     */
    public Map<String, Object> getReportJson(String reportId) {
        Report report = reportMapper.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> result = new LinkedHashMap<>();

            if (report.getSummary() != null) {
                result.put("summary", mapper.readValue(report.getSummary(), Map.class));
            }
            if (report.getDetails() != null) {
                result.put("details", mapper.readValue(report.getDetails(), Map.class));
            }
            if (report.getComparisons() != null) {
                result.put("comparisons", mapper.readValue(report.getComparisons(), Map.class));
            }

            return result;
        } catch (Exception e) {
            log.error("解析报告 JSON 失败：{}", e.getMessage());
            throw new BusinessException("解析报告失败");
        }
    }
}

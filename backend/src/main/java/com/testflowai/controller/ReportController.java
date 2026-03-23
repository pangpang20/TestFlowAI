package com.testflowai.controller;

import com.testflowai.common.BusinessException;
import com.testflowai.common.Result;
import com.testflowai.entity.Report;
import com.testflowai.service.ReportService;
import com.testflowai.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试报告控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 生成报告
     */
    @PostMapping("/generate/{executionId}")
    public Result<Report> generateReport(
            @PathVariable String executionId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            Report report = reportService.generateReport(executionId, userId);
            return Result.success(report);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("生成报告失败：{}", e.getMessage(), e);
            return Result.error("生成报告失败");
        }
    }

    /**
     * 获取报告详情
     */
    @GetMapping("/{reportId}")
    public Result<Report> getReport(@PathVariable String reportId) {
        try {
            Report report = reportService.getReport(reportId);
            if (report == null) {
                return Result.error("报告不存在");
            }
            return Result.success(report);
        } catch (Exception e) {
            log.error("获取报告失败：{}", e.getMessage(), e);
            return Result.error("获取报告失败");
        }
    }

    /**
     * 根据执行 ID 获取报告
     */
    @GetMapping("/execution/{executionId}")
    public Result<Report> getReportByExecutionId(@PathVariable String executionId) {
        try {
            Report report = reportService.getReportByExecutionId(executionId);
            if (report == null) {
                return Result.error("报告不存在");
            }
            return Result.success(report);
        } catch (Exception e) {
            log.error("获取报告失败：{}", e.getMessage(), e);
            return Result.error("获取报告失败");
        }
    }

    /**
     * 获取测试流的报告列表
     */
    @GetMapping("/test/{testId}")
    public Result<List<Report>> getReportsByTestId(@PathVariable String testId) {
        try {
            List<Report> reports = reportService.getReportsByTestId(testId);
            return Result.success(reports);
        } catch (Exception e) {
            log.error("获取报告列表失败：{}", e.getMessage(), e);
            return Result.error("获取报告列表失败");
        }
    }

    /**
     * 获取报告列表（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> getReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Report> reports = reportService.getReports(page, size);
            long total = reportService.getTotalReports();

            Map<String, Object> result = new HashMap<>();
            result.put("list", reports);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取报告列表失败：{}", e.getMessage(), e);
            return Result.error("获取报告列表失败");
        }
    }

    /**
     * 获取报告 JSON 数据
     */
    @GetMapping("/{reportId}/data")
    public Result<Map<String, Object>> getReportData(@PathVariable String reportId) {
        try {
            Map<String, Object> data = reportService.getReportJson(reportId);
            return Result.success(data);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取报告数据失败：{}", e.getMessage(), e);
            return Result.error("获取报告数据失败");
        }
    }

    /**
     * 下载报告为 JSON 文件
     */
    @GetMapping("/{reportId}/download")
    public ResponseEntity<?> downloadReport(@PathVariable String reportId) {
        try {
            Map<String, Object> data = reportService.getReportJson(reportId);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "report-" + reportId + ".json");

            return new ResponseEntity<>(json, headers, HttpStatus.OK);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("下载报告失败：{}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("下载报告失败");
        }
    }

    /**
     * 删除报告
     */
    @DeleteMapping("/{reportId}")
    public Result<?> deleteReport(
            @PathVariable String reportId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            String userId = getCurrentUserId(authorization);
            reportService.deleteReport(reportId, userId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除报告失败：{}", e.getMessage(), e);
            return Result.error("删除报告失败");
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

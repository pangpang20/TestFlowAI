package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.TestFlow;
import com.testflowai.service.TestFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试流控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/testflows")
public class TestFlowController {

    private static final Logger logger = LoggerFactory.getLogger(TestFlowController.class);

    private final TestFlowService testFlowService;

    public TestFlowController(TestFlowService testFlowService) {
        this.testFlowService = testFlowService;
    }

    /**
     * 获取所有测试流列表
     */
    @GetMapping
    public Result<List<TestFlow>> getAll() {
        List<TestFlow> testFlows = testFlowService.getAll();
        return Result.success(testFlows);
    }

    /**
     * 根据 ID 获取测试流详情
     */
    @GetMapping("/{id}")
    public Result<TestFlow> getById(@PathVariable String id) {
        TestFlow testFlow = testFlowService.getById(id);
        return Result.success(testFlow);
    }

    /**
     * 根据项目 ID 查询测试流
     */
    @GetMapping("/project/{projectId}")
    public Result<List<TestFlow>> getByProjectId(@PathVariable String projectId) {
        List<TestFlow> testFlows = testFlowService.getByProjectId(projectId);
        return Result.success(testFlows);
    }

    /**
     * 搜索测试流
     */
    @GetMapping("/search")
    public Result<List<TestFlow>> search(@RequestParam String keyword) {
        List<TestFlow> testFlows = testFlowService.searchByTitle(keyword);
        return Result.success(testFlows);
    }

    /**
     * 创建测试流
     */
    @PostMapping
    public Result<TestFlow> create(@RequestBody TestFlow testFlow) {
        TestFlow created = testFlowService.create(testFlow);
        return Result.success("创建成功", created);
    }

    /**
     * 更新测试流
     */
    @PutMapping("/{id}")
    public Result<TestFlow> update(@PathVariable String id, @RequestBody TestFlow testFlow) {
        TestFlow updated = testFlowService.update(id, testFlow);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除测试流
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        testFlowService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 复制测试流
     */
    @PostMapping("/{id}/duplicate")
    public Result<TestFlow> duplicate(@PathVariable String id) {
        TestFlow duplicated = testFlowService.duplicate(id);
        return Result.success("复制成功", duplicated);
    }

    /**
     * 导入测试流 JSON
     */
    @PostMapping("/import")
    public Result<TestFlow> importTestFlow(@RequestBody Map<String, Object> data) {
        TestFlow testFlow = new TestFlow();
        testFlow.setTitle((String) data.get("title"));
        testFlow.setVersion((String) data.get("version"));
        testFlow.setAppUrl((String) data.get("appUrl"));
        testFlow.setSteps((String) data.get("steps"));
        testFlow.setVariables((String) data.get("variables"));
        testFlow.setTags((String) data.get("tags"));
        testFlow.setExpectedReport((String) data.get("expectedReport"));

        TestFlow created = testFlowService.create(testFlow);
        return Result.success("导入成功", created);
    }

    /**
     * 导出测试流 JSON
     */
    @GetMapping("/{id}/export")
    public Result<Map<String, Object>> export(@PathVariable String id) {
        TestFlow testFlow = testFlowService.getById(id);
        // 返回测试流的 JSON 数据
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("testId", testFlow.getTestId());
        data.put("title", testFlow.getTitle());
        data.put("version", testFlow.getVersion());
        data.put("appUrl", testFlow.getAppUrl());
        data.put("steps", testFlow.getSteps());
        data.put("variables", testFlow.getVariables());
        data.put("tags", testFlow.getTags());
        return Result.success(data);
    }
}

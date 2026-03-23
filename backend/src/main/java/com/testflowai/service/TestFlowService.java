package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.TestFlow;
import com.testflowai.mapper.TestFlowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 测试流服务类
 * @author TestFlowAI
 */
@Service
public class TestFlowService {

    private static final Logger logger = LoggerFactory.getLogger(TestFlowService.class);

    private final TestFlowMapper testFlowMapper;

    public TestFlowService(TestFlowMapper testFlowMapper) {
        this.testFlowMapper = testFlowMapper;
    }

    /**
     * 根据 ID 查询测试流
     */
    public TestFlow getById(String testId) {
        TestFlow testFlow = testFlowMapper.selectById(testId);
        if (testFlow == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "测试流不存在");
        }
        return testFlow;
    }

    /**
     * 获取所有测试流列表
     */
    public List<TestFlow> getAll() {
        return testFlowMapper.selectAll();
    }

    /**
     * 根据项目 ID 查询测试流
     */
    public List<TestFlow> getByProjectId(String projectId) {
        return testFlowMapper.selectByProjectId(projectId);
    }

    /**
     * 搜索测试流
     */
    public List<TestFlow> searchByTitle(String keyword) {
        return testFlowMapper.searchByTitle(keyword);
    }

    /**
     * 创建测试流
     */
    @Transactional(rollbackFor = Exception.class)
    public TestFlow create(TestFlow testFlow) {
        // 检查标题是否存在
        if (testFlowMapper.existsByTitle(testFlow.getTitle())) {
            throw new BusinessException("测试流标题已存在");
        }

        // 设置默认版本号
        if (testFlow.getVersion() == null) {
            testFlow.setVersion("1.0.0");
        }

        // 设置 UUID
        testFlow.setTestId(UUID.randomUUID().toString());

        testFlowMapper.insert(testFlow);
        logger.info("创建测试流成功：{}", testFlow.getTitle());

        return testFlow;
    }

    /**
     * 更新测试流
     */
    @Transactional(rollbackFor = Exception.class)
    public TestFlow update(String testId, TestFlow testFlow) {
        // 检查测试流是否存在
        TestFlow existing = testFlowMapper.selectById(testId);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "测试流不存在");
        }

        // 如果修改了标题，检查新标题是否已存在
        if (testFlow.getTitle() != null && !testFlow.getTitle().equals(existing.getTitle())) {
            if (testFlowMapper.existsByTitle(testFlow.getTitle())) {
                throw new BusinessException("测试流标题已存在");
            }
        }

        testFlowMapper.update(testFlow);
        logger.info("更新测试流成功：{}", testId);

        return testFlow;
    }

    /**
     * 删除测试流（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String testId) {
        TestFlow testFlow = testFlowMapper.selectById(testId);
        if (testFlow == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "测试流不存在");
        }

        testFlowMapper.deleteById(testId);
        logger.info("删除测试流成功：{}", testId);
    }

    /**
     * 复制测试流
     */
    @Transactional(rollbackFor = Exception.class)
    public TestFlow duplicate(String testId) {
        TestFlow original = testFlowMapper.selectById(testId);
        if (original == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "测试流不存在");
        }

        TestFlow copy = new TestFlow();
        copy.setTitle(original.getTitle() + " (副本)");
        copy.setVersion("1.0.0");
        copy.setAppUrl(original.getAppUrl());
        copy.setSteps(original.getSteps());
        copy.setVariables(original.getVariables());
        copy.setTags(original.getTags());
        copy.setExpectedReport(original.getExpectedReport());
        copy.setProjectId(original.getProjectId());

        copy.setTestId(UUID.randomUUID().toString());
        testFlowMapper.insert(copy);

        logger.info("复制测试流成功：{} -> {}", testId, copy.getTestId());
        return copy;
    }
}

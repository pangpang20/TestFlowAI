package com.testflowai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行日志推送服务
 * @author TestFlowAI
 */
@Service
public class ExecutionLogService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionLogService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 存储执行中的会话
    private final Map<String, String> executionSessions = new ConcurrentHashMap<>();

    /**
     * 推送步骤开始消息
     */
    public void pushStepStart(String executionId, int stepId, String description) {
        Map<String, Object> message = createMessage("step_start", executionId);
        message.put("stepId", stepId);
        message.put("description", description);
        message.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, message);
        log.info("推送步骤开始消息：executionId={}, stepId={}", executionId, stepId);
    }

    /**
     * 推送步骤结束消息
     */
    public void pushStepEnd(String executionId, int stepId, String status, String errorMessage) {
        Map<String, Object> message = createMessage("step_end", executionId);
        message.put("stepId", stepId);
        message.put("status", status);
        message.put("errorMessage", errorMessage);
        message.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, message);
        log.info("推送步骤结束消息：executionId={}, stepId={}, status={}", executionId, stepId, status);
    }

    /**
     * 推送日志消息
     */
    public void pushLog(String executionId, String message) {
        Map<String, Object> logMessage = createMessage("log", executionId);
        logMessage.put("content", message);
        logMessage.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, logMessage);
    }

    /**
     * 推送错误消息
     */
    public void pushError(String executionId, String errorMessage) {
        Map<String, Object> message = createMessage("error", executionId);
        message.put("errorMessage", errorMessage);
        message.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, message);
    }

    /**
     * 推送执行完成消息
     */
    public void pushComplete(String executionId, String status, int totalSteps, int passedSteps, int failedSteps) {
        Map<String, Object> message = createMessage("complete", executionId);
        message.put("status", status);
        message.put("totalSteps", totalSteps);
        message.put("passedSteps", passedSteps);
        message.put("failedSteps", failedSteps);
        message.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, message);
        log.info("推送执行完成消息：executionId={}, status={}", executionId, status);
    }

    /**
     * 推送截图通知
     */
    public void pushScreenshot(String executionId, int stepId, String screenshotPath) {
        Map<String, Object> message = createMessage("screenshot", executionId);
        message.put("stepId", stepId);
        message.put("screenshotPath", screenshotPath);
        message.put("timestamp", System.currentTimeMillis());

        sendMessage("/topic/execution." + executionId, message);
    }

    /**
     * 发送消息
     */
    private void sendMessage(String destination, Map<String, Object> message) {
        try {
            messagingTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            log.error("发送 WebSocket 消息失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 创建基础消息
     */
    private Map<String, Object> createMessage(String type, String executionId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("executionId", executionId);
        return message;
    }

    /**
     * 注册执行会话
     */
    public void registerSession(String executionId, String sessionId) {
        executionSessions.put(executionId, sessionId);
        log.info("注册执行会话：executionId={}, sessionId={}", executionId, sessionId);
    }

    /**
     * 移除执行会话
     */
    public void removeSession(String executionId) {
        executionSessions.remove(executionId);
        log.info("移除执行会话：executionId={}", executionId);
    }
}

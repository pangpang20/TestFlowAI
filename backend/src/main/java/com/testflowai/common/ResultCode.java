package com.testflowai.common;

import lombok.Getter;

/**
 * 统一响应状态码
 * @author TestFlowAI
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    TOKEN_INVALID(401, "Token 无效"),
    TOKEN_EXPIRED(401, "Token 已过期"),
    LOGIN_FAILED(401, "登录失败"),
    USER_DISABLED(403, "用户已被禁用"),
    DUPLICATE_USERNAME(400, "用户名已存在"),
    DUPLICATE_EMAIL(400, "邮箱已存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

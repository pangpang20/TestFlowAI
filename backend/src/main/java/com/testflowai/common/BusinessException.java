package com.testflowai.common;

import lombok.Getter;

/**
 * 业务异常类
 * @author TestFlowAI
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 构造业务异常
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }

    /**
     * 构造业务异常
     * @param code 错误码
     * @param message 异常消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常
     * @param resultCode 错误码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 构造业务异常
     * @param resultCode 错误码枚举
     * @param message 异常消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}

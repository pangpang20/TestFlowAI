package com.testflowai.mapper;

import com.testflowai.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 审计日志 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface AuditLogMapper {

    /**
     * 根据 ID 查询审计日志
     * @param logId 日志 ID
     * @return 审计日志信息
     */
    AuditLog selectById(@Param("logId") String logId);

    /**
     * 根据用户 ID 查询审计日志列表
     * @param userId 用户 ID
     * @return 审计日志列表
     */
    List<AuditLog> selectByUserId(@Param("userId") String userId);

    /**
     * 根据时间范围查询审计日志列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> selectByTimeRange(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询所有审计日志
     * @return 审计日志列表
     */
    List<AuditLog> selectAll();

    /**
     * 插入审计日志
     * @param auditLog 审计日志信息
     * @return 影响行数
     */
    int insert(AuditLog auditLog);

    /**
     * 根据 ID 删除审计日志
     * @param logId 日志 ID
     * @return 影响行数
     */
    int deleteById(@Param("logId") String logId);

    /**
     * 清理指定时间之前的日志
     * @param beforeTime 清理该时间之前的日志
     * @return 影响行数
     */
    int deleteBeforeTime(@Param("beforeTime") Date beforeTime);
}

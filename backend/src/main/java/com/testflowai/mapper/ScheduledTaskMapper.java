package com.testflowai.mapper;

import com.testflowai.entity.ScheduledTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Date;

/**
 * 定时任务 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface ScheduledTaskMapper {

    /**
     * 插入定时任务
     */
    int insert(ScheduledTask task);

    /**
     * 更新定时任务
     */
    int update(ScheduledTask task);

    /**
     * 根据 ID 删除定时任务
     */
    int deleteById(@Param("taskId") String taskId);

    /**
     * 根据 ID 查询定时任务
     */
    ScheduledTask getById(@Param("taskId") String taskId);

    /**
     * 查询定时任务列表
     */
    List<ScheduledTask> list(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计定时任务总数
     */
    long count();

    /**
     * 根据状态查询定时任务
     */
    List<ScheduledTask> getByStatus(@Param("status") String status);

    /**
     * 获取需要执行的任务
     */
    List<ScheduledTask> getDueTasks(@Param("currentTime") Date currentTime);

    /**
     * 更新执行时间
     */
    int updateExecutionTime(@Param("taskId") String taskId,
                            @Param("lastExecutedAt") Date lastExecutedAt,
                            @Param("nextExecutionAt") Date nextExecutionAt);

    /**
     * 根据测试流 ID 查询定时任务
     */
    List<ScheduledTask> getByTestId(@Param("testId") String testId);
}

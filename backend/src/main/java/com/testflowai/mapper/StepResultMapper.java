package com.testflowai.mapper;

import com.testflowai.entity.StepResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 步骤执行结果 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface StepResultMapper {

    /**
     * 插入步骤结果
     */
    int insert(StepResult stepResult);

    /**
     * 批量插入步骤结果
     */
    int batchInsert(@Param("list") List<StepResult> stepResults);

    /**
     * 更新步骤结果
     */
    int update(StepResult stepResult);

    /**
     * 根据 ID 删除步骤结果
     */
    int deleteById(@Param("resultId") String resultId);

    /**
     * 根据执行 ID 删除步骤结果
     */
    int deleteByExecutionId(@Param("executionId") String executionId);

    /**
     * 根据执行 ID 查询步骤结果列表
     */
    List<StepResult> getByExecutionId(@Param("executionId") String executionId);

    /**
     * 根据执行 ID 和步骤 ID 查询步骤结果
     */
    StepResult getByIds(@Param("executionId") String executionId, @Param("stepId") Integer stepId);

    /**
     * 更新步骤状态
     */
    int updateStatus(@Param("resultId") String resultId, @Param("status") String status);

    /**
     * 统计执行中各状态的步骤数量
     */
    List<StepStatusCount> countByStatus(@Param("executionId") String executionId);

    /**
     * 状态统计内部类
     */
    class StepStatusCount {
        private String status;
        private long count;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}

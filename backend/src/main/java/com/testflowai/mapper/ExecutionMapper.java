package com.testflowai.mapper;

import com.testflowai.entity.Execution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 执行记录 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface ExecutionMapper {

    /**
     * 插入执行记录
     */
    int insert(Execution execution);

    /**
     * 更新执行记录
     */
    int update(Execution execution);

    /**
     * 根据 ID 删除执行记录
     */
    int deleteById(@Param("executionId") String executionId);

    /**
     * 根据 ID 查询执行记录
     */
    Execution getById(@Param("executionId") String executionId);

    /**
     * 根据测试流 ID 查询执行记录列表
     */
    List<Execution> getByTestId(@Param("testId") String testId);

    /**
     * 查询执行记录列表（分页）
     */
    List<Execution> list(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计执行记录总数
     */
    long count();

    /**
     * 根据状态查询执行记录
     */
    List<Execution> getByStatus(@Param("status") String status);

    /**
     * 更新执行状态
     */
    int updateStatus(@Param("executionId") String executionId, @Param("status") String status);

    /**
     * 统计各状态的执行数量
     */
    List<ExecutionStatusCount> countByStatus();

    /**
     * 获取最近的执行记录
     */
    List<Execution> getRecentExecutions(@Param("limit") int limit);

    /**
     * 状态统计内部类
     */
    class ExecutionStatusCount {
        private String status;
        private long count;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}

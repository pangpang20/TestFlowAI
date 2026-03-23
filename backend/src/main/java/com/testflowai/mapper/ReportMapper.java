package com.testflowai.mapper;

import com.testflowai.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 测试报告 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface ReportMapper {

    /**
     * 插入报告
     */
    int insert(Report report);

    /**
     * 更新报告
     */
    int update(Report report);

    /**
     * 根据 ID 删除报告
     */
    int deleteById(@Param("reportId") String reportId);

    /**
     * 根据 ID 查询报告
     */
    Report getById(@Param("reportId") String reportId);

    /**
     * 根据执行 ID 查询报告
     */
    Report getByExecutionId(@Param("executionId") String executionId);

    /**
     * 根据测试流 ID 查询报告列表
     */
    List<Report> getByTestId(@Param("testId") String testId);

    /**
     * 查询报告列表（分页）
     */
    List<Report> list(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计报告总数
     */
    long count();
}

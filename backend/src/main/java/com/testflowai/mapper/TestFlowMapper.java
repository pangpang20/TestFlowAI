package com.testflowai.mapper;

import com.testflowai.entity.TestFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试流 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface TestFlowMapper {

    /**
     * 根据 ID 查询测试流
     */
    TestFlow selectById(@Param("testId") String testId);

    /**
     * 根据 ID 查询测试流（别名方法）
     */
    default TestFlow getById(String testId) {
        return selectById(testId);
    }

    /**
     * 查询所有测试流
     */
    List<TestFlow> selectAll();

    /**
     * 根据项目 ID 查询测试流
     */
    List<TestFlow> selectByProjectId(@Param("projectId") String projectId);

    /**
     * 搜索测试流
     */
    List<TestFlow> searchByTitle(@Param("keyword") String keyword);

    /**
     * 插入测试流
     */
    int insert(TestFlow testFlow);

    /**
     * 更新测试流
     */
    int update(TestFlow testFlow);

    /**
     * 根据 ID 删除测试流（逻辑删除）
     */
    int deleteById(@Param("testId") String testId);

    /**
     * 检查标题是否存在
     */
    boolean existsByTitle(@Param("title") String title);
}

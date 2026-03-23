package com.testflowai.mapper;

import com.testflowai.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface ProjectMapper {

    /**
     * 根据 ID 查询项目
     * @param projectId 项目 ID
     * @return 项目信息
     */
    Project selectById(@Param("projectId") String projectId);

    /**
     * 查询所有项目
     * @return 项目列表
     */
    List<Project> selectAll();

    /**
     * 根据状态查询项目
     * @param status 项目状态
     * @return 项目列表
     */
    List<Project> selectByStatus(@Param("status") String status);

    /**
     * 搜索项目
     * @param keyword 关键词
     * @return 项目列表
     */
    List<Project> searchByName(@Param("keyword") String keyword);

    /**
     * 插入项目
     * @param project 项目信息
     * @return 影响行数
     */
    int insert(Project project);

    /**
     * 更新项目
     * @param project 项目信息
     * @return 影响行数
     */
    int update(Project project);

    /**
     * 根据 ID 删除项目（逻辑删除）
     * @param projectId 项目 ID
     * @return 影响行数
     */
    int deleteById(@Param("projectId") String projectId);

    /**
     * 检查项目名称是否存在
     * @param projectName 项目名称
     * @return 是否存在
     */
    boolean existsByName(@Param("projectName") String projectName);
}

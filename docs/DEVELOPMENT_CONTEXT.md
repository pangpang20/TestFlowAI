# TestFlowAI 开发上下文约定

## 项目概述
TestFlowAI 是一个基于 AI 智能体的自动化测试流程管理平台，支持测试用例的智能生成、执行跟踪和报告分析。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.3
- **JDK**: 17
- **ORM**: MyBatis 3.x
- **安全**: Spring Security 6.x + JWT
- **数据库**: MySQL 8.0
- **构建**: Maven

### 前端
- **框架**: Vue 3 + TypeScript
- **构建**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **UI**: Element Plus

### 基础设施
- **容器**: Docker + Docker Compose
- **数据库**: MySQL 8.0
- **对象存储**: MinIO

## 代码规范约定

### 后端代码规范

#### 实体类 (Entity)
- 包路径：`com.testflowai.entity`
- 命名：使用驼峰命名，与数据库表名对应
- 必须实现 `Serializable` 接口
- 使用 Lombok 的 `@Data` 注解
- 字段必须包含：id, createdAt, updatedAt, deletedAt, deleted, createdBy, updatedBy, deletedBy

```java
package com.testflowai.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class TestFlow implements Serializable {
    private static final long serialVersionUID = 1L;

    private String testId;
    private String title;
    // ... 其他字段
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Integer deleted;
    private String createdBy;
    private String updatedBy;
    private String deletedBy;
}
```

#### Mapper 接口
- 包路径：`com.testflowai.mapper`
- 命名：`{实体名}Mapper.java`
- 使用 MyBatis `@Mapper` 注解
- 方法命名规范：
  - 查询单个：`selectById`, `getByXXX`
  - 查询列表：`selectAll`, `list`, `getByXXX`
  - 插入：`insert`
  - 更新：`update`
  - 删除：`deleteById`

```java
@Mapper
public interface TestFlowMapper {
    TestFlow selectById(@Param("testId") String testId);
    List<TestFlow> selectAll();
    int insert(TestFlow testFlow);
    int update(TestFlow testFlow);
    int deleteById(@Param("testId") String testId);
}
```

#### MyBatis XML Mapper
- 路径：`src/main/resources/mapper/{Mapper 名}.xml`
- namespace 必须对应 Mapper 接口全路径
- 使用 `BaseResultMap` 作为标准结果映射
- 使用 `Base_Column_List` 作为标准列列表

#### Service 层
- 包路径：`com.testflowai.service`
- 命名：`{实体名}Service.java`
- 使用 `@Service` 注解
- 业务异常使用 `BusinessException`
- 事务管理使用 `@Transactional`

```java
@Service
public class TestFlowService {
    @Autowired
    private TestFlowMapper testFlowMapper;

    public TestFlow getTestFlow(String testId) {
        TestFlow testFlow = testFlowMapper.selectById(testId);
        if (testFlow == null || testFlow.getDeleted() == 1) {
            throw new BusinessException("测试流不存在");
        }
        return testFlow;
    }
}
```

#### Controller 层
- 包路径：`com.testflowai.controller`
- 命名：`{实体名}Controller.java`
- 使用 `@RestController` 和 `@RequestMapping` 注解
- 跨域配置使用 `@CrossOrigin`
- 统一返回 `Result<T>` 对象
- API 路径规范：`/api/{资源名}`

```java
@RestController
@RequestMapping("/api/testflows")
@CrossOrigin(origins = "*")
public class TestFlowController {
    @Autowired
    private TestFlowService testFlowService;

    @GetMapping("/{id}")
    public Result<TestFlow> getTestFlow(@PathVariable String id) {
        try {
            TestFlow testFlow = testFlowService.getTestFlow(id);
            return Result.success(testFlow);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
}
```

#### 通用响应格式
```java
public class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
```

### 前端代码规范

#### API 模块
- 路径：`src/api/{模块名}.ts`
- 使用 Axios 封装的 `request` 工具
- 函数命名：`{操作}{实体}Api`，如 `getTestFlowsApi`, `createTestFlowApi`
- 使用 TypeScript 类型定义

```typescript
import request from '@/utils/request'
import type { TestFlow } from '@/types/testflow'

export function getTestFlowsApi() {
  return request<TestFlow[]>({
    url: '/api/testflows',
    method: 'GET'
  })
}
```

#### 类型定义
- 路径：`src/types/{实体名}.ts`
- 使用 TypeScript interface
- 字段类型明确

```typescript
export interface TestFlow {
  testId: string
  title: string
  version: string
  appUrl: string
  steps: string
  variables: string
  tags: string
  createdAt: string | null
  updatedAt: string | null
}
```

#### 视图组件
- 路径：`src/views/{页面名}.vue`
- 使用 Vue 3 Composition API
- 使用 `<script setup lang="ts">` 语法
- 使用 Element Plus 组件库

#### 路由配置
- 路径：`src/router/index.ts`
- 路由命名使用大驼峰：`Tests`, `Projects`, `Executions`
- 页面标题使用中文

## 数据库规范

### 表命名
- 前缀：`t_`
- 名称：小写，下划线分隔
- 示例：`t_user`, `t_testflow`, `t_execution`

### 字段规范
- 主键：`{实体}_id`，如 `user_id`, `test_id`
- 创建时间：`created_at`，默认 `CURRENT_TIMESTAMP`
- 更新时间：`updated_at`，默认 `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
- 删除时间：`deleted_at`，默认 `NULL`
- 软删除标记：`deleted`，TINYINT，默认 0
- 审计字段：`created_by`, `updated_by`, `deleted_by`

### 软删除模式
所有删除操作使用软删除：
```sql
UPDATE t_table SET deleted = 1, deleted_at = NOW(), deleted_by = ? WHERE id = ? AND deleted = 0
```

查询时始终添加条件：
```sql
SELECT * FROM t_table WHERE deleted = 0
```

## 已实现功能

### 认证与授权
- JWT Token 认证
- RBAC 权限控制
- 角色继承
- 审计日志

### 用户管理
- 用户 CRUD
- 头像上传（MinIO）
- 密码修改

### 项目管理
- 项目 CRUD
- 项目进度更新
- 项目状态管理
- 项目成员管理

### 测试流管理
- 测试流 CRUD
- 测试流复制
- 测试流导入/导出
- 测试流搜索

### Chrome 扩展录制器
- 浏览器操作捕获（点击、输入、选择等）
- CSS 选择器自动生成
- 录制操作实时预览
- 直接保存到 TestFlowAI

### 测试执行引擎
- 执行记录管理
- 步骤结果跟踪
- 执行状态统计
- 模拟执行（待集成 Playwright）
- 定时任务调度执行

### 定时任务
- Cron 表达式配置
- 任务状态管理（激活/停用）
- 手动触发执行
- 自动调度执行
- 执行时间跟踪

### 测试报告
- 报告自动生成
- 报告详情展示
- 步骤执行结果
- 报告下载（JSON）

## 待实现功能
- Playwright 回放集成
- AI 测试用例生成
- 测试用例智能修复
- 定时任务执行测试

## 配置管理

### 后端配置
- 开发环境：`application-dev.yml`
- 生产环境：`application-prod.yml`
- 敏感配置使用环境变量

### 前端配置
- `.env` 文件管理环境变量
- `VITE_API_BASE_URL` 配置后端地址

## 开发流程

### 新增功能
1. 创建数据库表（如需要）
2. 编写 Entity 实体类
3. 编写 Mapper 接口和 XML
4. 编写 Service 业务逻辑
5. 编写 Controller API
6. 前端编写 API 模块
7. 前端编写视图组件
8. 更新路由配置
9. 更新导航菜单
10. 编写测试用例

### 代码提交
- 使用有意义的提交信息
- 功能开发使用 feature 分支
- 提交前确保编译通过

## 常用命令

### 后端
```bash
cd backend
mvn clean compile          # 编译
mvn test                   # 测试
mvn spring-boot:run        # 运行
mvn package                # 打包
```

### 前端
```bash
cd frontend
npm run dev               # 开发模式
npm run build             # 构建
npm run preview           # 预览
```

### Docker
```bash
cd docker
docker-compose up -d      # 启动服务
docker-compose down       # 停止服务
docker-compose logs -f    # 查看日志
```

## 端口配置
- 后端服务：18080
- 前端服务：3001
- MySQL: 3306
- MinIO API: 29000
- MinIO Console: 29001

## 默认账户
- 管理员：admin / admin123
- MinIO: minioadmin / minioadmin123

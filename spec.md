# TestFlowAI 代码规范文档

## 1. 项目概述

TestFlowAI 是一个测试流程管理平台，提供测试用例管理、执行跟踪、报告生成等功能。

## 2. 技术栈

- **后端框架**: Spring Boot 3.x
- **JDK 版本**: 17
- **构建工具**: Maven 3.8+
- **数据库**: MySQL 8.0
- **ORM 框架**: MyBatis 3.x
- **安全框架**: Spring Security 6.x
- **认证方式**: JWT (JSON Web Token)

## 3. 项目结构规范

### 3.1 包结构
```
com.testflowai
├── config/        # 配置类
├── common/        # 通用类（结果封装、异常等）
├── exception/     # 全局异常处理
├── entity/        # 实体类
├── mapper/        # MyBatis Mapper 接口和 XML
├── service/       # 业务逻辑层
├── controller/    # REST API 控制器
└── util/          # 工具类
```

### 3.2 命名规范
- 实体类：使用单数名词，如 `User`, `Role`
- Mapper 接口：以 `Mapper` 结尾，如 `UserMapper`
- Service 类：以 `Service` 结尾，如 `UserService`
- Controller 类：以 `Controller` 结尾，如 `UserController`
- 配置类：以 `Config` 结尾，如 `SecurityConfig`

## 4. 代码规范

### 4.1 统一响应格式
所有 API 响应必须使用 `Result<T>` 封装：
```java
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1679876543210
}
```

### 4.2 错误码规范
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

### 4.3 异常处理
- 业务异常使用 `BusinessException`
- 全局异常处理器捕获所有异常并返回统一格式

### 4.4 安全规范
- 密码使用 BCrypt 加密
- 认证使用 JWT Token，有效期 24 小时
- 所有 API 需要认证（除登录接口外）
- 启用 CORS 支持前端跨域请求

## 5. 数据库规范

### 5.1 表命名
- 使用小写字母和下划线，如 `sys_user`, `sys_role`

### 5.2 字段命名
- 主键：`id` (BIGINT)
- 创建时间：`create_time` (DATETIME)
- 更新时间：`update_time` (DATETIME)
- 逻辑删除：`deleted` (TINYINT, 0-正常，1-删除)

### 5.3 索引规范
- 主键索引：PRIMARY KEY
- 唯一索引：UK_字段名
- 普通索引：IDX_字段名

## 6. 测试规范

- 使用 JUnit 5 进行测试
- Service 层使用 Mock 进行测试
- 测试覆盖率目标：80% 以上
- 测试类命名：被测试类名 + Test

## 7. 注释规范

- 所有公共类和方法必须有 Javadoc 注释
- 复杂逻辑必须有行内注释
- 使用中文注释

## 8. API 接口规范

### 8.1 认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/logout - 用户登出

### 8.2 用户接口
- GET /api/users - 获取用户列表
- GET /api/users/{id} - 获取用户详情
- POST /api/users - 创建用户
- PUT /api/users/{id} - 更新用户
- DELETE /api/users/{id} - 删除用户

## 9. 配置规范

- 开发环境：application-dev.yml
- 生产环境：application-prod.yml
- 敏感配置使用环境变量

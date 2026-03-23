# TestFlowAI 开发进度

## 已完成 (2026-03-21)

### 1. 数据库层
- [x] Docker Compose 配置 (`docker/docker-compose.yml`)
- [x] MySQL 8.0 容器启动并运行
- [x] 数据库初始化脚本 (`docker/init-sql/init.sql`)
- [x] 12 张表已创建：
  - 用户相关：t_user, t_role, t_permission, t_user_role, t_role_permission, t_role_inheritance, t_audit_log
  - 测试流相关：t_testflow, t_testflow_version
  - 执行相关：t_execution, t_step_result
  - 报告相关：t_report
- [x] 初始数据已插入（管理员账户 admin/admin123）

### 2. 后端 (Spring Boot 3.x)
- [x] 项目结构创建 (`backend/`)
- [x] Maven 配置 (pom.xml) - Spring Boot 3.2.3, MyBatis, MySQL, Lombok, JWT
- [x] 启动类 `TestFlowAiApplication.java`
- [x] 配置类：
  - `SecurityConfig.java` - Spring Security + JWT 配置
  - `MyBatisConfig.java` - MyBatis 配置
  - `CorsConfig.java` - CORS 跨域配置
  - `JwtAuthenticationFilter.java` - JWT 认证过滤器
- [x] 通用类：
  - `Result.java` - 统一响应格式
  - `ResultCode.java` - 响应状态码枚举
  - `BusinessException.java` - 业务异常类
- [x] 全局异常处理 `GlobalExceptionHandler.java`
- [x] 实体类：User, Role, Permission, AuditLog
- [x] Mapper 接口和 XML 文件
- [x] 服务类：
  - `AuthService.java` - 认证服务
  - `UserService.java` - 用户服务
- [x] 控制器：
  - `AuthController.java` - /api/auth/login, /api/auth/logout
  - `UserController.java` - /api/users
- [x] 工具类：
  - `JwtUtil.java` - JWT 生成/解析/验证
  - `PasswordUtil.java` - BCrypt 密码加密
- [x] 单元测试：
  - `AuthServiceTest.java`
  - `UserServiceTest.java`

### 3. 前端 (Vue 3 + TypeScript)
- [x] 项目结构创建 (`frontend/`)
- [x] Vite 配置 (`vite.config.ts`)
- [x] TypeScript 配置 (`tsconfig.json`)
- [x] Tailwind CSS 配置 (`tailwind.config.js`)
- [x] 环境配置 (`.env`)
- [x] 入口文件 `main.ts`
- [x] 根组件 `App.vue`
- [x] 路由配置 `router/index.ts` - 含路由守卫
- [x] Pinia Store：
  - `stores/user.ts` - 用户状态管理
  - `stores/index.ts`
- [x] API 模块：
  - `api/auth.ts` - 认证 API
  - `api/user.ts` - 用户 API
- [x] 工具类 `utils/request.ts` - Axios 封装
- [x] 样式 `styles/main.css`
- [x] 页面组件：
  - `views/Login.vue` - 登录页面
  - `views/Dashboard.vue` - 仪表盘

---

## 下一步工作

### 后端
1. 运行 `mvn test` 验证单元测试
2. 实现测试流模块（TestFlow CRUD）
3. 实现执行模块（Execution）
4. 实现报告模块（Report）
5. 集成 Playwright 回放引擎

### 前端
1. 运行 `npm install` 安装依赖
2. 实现测试流列表页面
3. 实现测试流编辑器
4. 实现执行回放页面
5. 实现测试报告页面

### 验证方式

**后端验证**：
```bash
cd /opt/TestFlowAI/backend
mvn test
```

**前端验证**：
```bash
cd /opt/TestFlowAI/frontend
npm install
npm run dev
```

**数据库验证**：
```bash
docker exec testflowai-mysql mysql -uroot -pTestFlowAI2026 testflowai -e "SHOW TABLES;"
```

---

## Docker 配置

**启动 MySQL**：
```bash
cd /opt/TestFlowAI/docker
docker-compose up -d mysql
```

**查看日志**：
```bash
docker-compose logs -f mysql
```

**停止服务**：
```bash
docker-compose down
```

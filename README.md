# TestFlowAI

基于 AI 智能体的自动化测试流程管理平台

## 项目简介

TestFlowAI 是一个集成了 AI 能力的测试自动化平台，支持测试用例的智能生成、执行跟踪和报告分析。

## 功能特性

- 🤖 AI 智能体驱动的测试流程编排
- 📋 测试用例自动生成与管理
- 🔐 JWT 认证 + RBAC 权限控制
- 📊 实时测试执行跟踪与报告
- 🔗 支持多种测试框架集成 (Playwright, JUnit 等)
- 🐳 Docker 容器化部署

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
- **UI**: Tailwind CSS
- **HTTP**: Axios

### 基础设施
- **容器**: Docker + Docker Compose
- **数据库**: MySQL 8.0

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- Maven 3.8+
- Docker & Docker Compose

### 1. 启动数据库

```bash
cd docker
docker-compose up -d mysql
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
# 默认端口：8080
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 默认端口：5173
```

### 4. 访问应用

打开浏览器访问 `http://localhost:5173`

默认管理员账户:
- 用户名：`admin`
- 密码：`admin123`

## 项目结构

```
TestFlowAI/
├── backend/          # Spring Boot 后端服务
│   ├── src/main/java/com/testflowai/
│   │   ├── common/      # 通用类 (Result, BusinessException)
│   │   ├── config/      # 配置类 (Security, Cors, MyBatis)
│   │   ├── controller/  # REST API 控制器
│   │   ├── entity/      # 实体类
│   │   ├── exception/   # 全局异常处理
│   │   ├── mapper/      # MyBatis Mapper 接口
│   │   ├── service/     # 业务逻辑层
│   │   └── util/        # 工具类 (JwtUtil, PasswordUtil)
│   └── src/main/resources/
│       ├── application.yml      # 主配置
│       ├── application-dev.yml  # 开发环境配置
│       └── mapper/              # MyBatis XML 映射文件
├── frontend/         # Vue 3 前端应用
│   ├── src/
│   │   ├── api/         # API 模块
│   │   ├── components/  # 通用组件
│   │   ├── router/      # 路由配置
│   │   ├── stores/      # Pinia Store
│   │   ├── styles/      # 全局样式
│   │   ├── utils/       # 工具函数
│   │   └── views/       # 页面组件
│   └── package.json
├── docker/           # Docker 配置
│   ├── docker-compose.yml  # 服务编排
│   └── init-sql/         # 数据库初始化脚本
├── spec.md           # 代码规范文档
└── DEV_PROGRESS.md   # 开发进度记录
```

## API 接口

### 认证接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |

### 用户接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users | 获取用户列表 |
| GET | /api/users/{id} | 获取用户详情 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

## 开发进度

详见 [DEV_PROGRESS.md](DEV_PROGRESS.md)

## 代码规范

详见 [spec.md](spec.md)

## 开发计划

### 待实现功能
- [ ] 测试流 (TestFlow) CRUD
- [ ] 测试执行引擎
- [ ] Playwright 回放集成
- [ ] 测试报告页面
- [ ] AI 测试用例生成

## 贡献指南

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

MIT License

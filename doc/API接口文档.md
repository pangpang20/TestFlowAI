# TestFlowAI API 接口文档

## 目录

1. [接口概述](#1-接口概述)
2. [认证接口](#2-认证接口)
3. [用户接口](#3-用户接口)
4. [项目接口](#4-项目接口)
5. [测试流接口](#5-测试流接口)
6. [执行接口](#6-执行接口)
7. [报告接口](#7-报告接口)
8. [定时任务接口](#8-定时任务接口)
9. [统计接口](#9-统计接口)
10. [文件上传接口](#10-文件上传接口)
11. [系统接口](#11-系统接口)
12. [错误码说明](#12-错误码说明)

---

## 1. 接口概述

### 1.1 基础信息

- **API 版本**: v1
- **基础路径**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

### 1.2 认证方式

除登录接口外，所有接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer <your_token>
```

### 1.3 响应格式

所有接口响应统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1774274622604,
  "success": true
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | number | 状态码（200=成功，401=未授权，403=禁止访问，500=服务器错误） |
| message | string | 响应消息 |
| data | object/array | 响应数据 |
| timestamp | number | 响应时间戳 |
| success | boolean | 请求是否成功 |

### 1.4 分页参数

列表接口通常支持分页，使用以下参数：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | number | 1 | 页码（从 1 开始） |
| size | number | 10 | 每页数量 |

分页响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "list": []
  },
  "timestamp": 1774274622604,
  "success": true
}
```

---

## 2. 认证接口

### 2.1 用户登录

**接口**: `POST /api/auth/login`

**权限**: 公开

**请求参数：**

```json
{
  "username": "admin",
  "password": "password"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "user": {
      "userId": "user-admin-001",
      "username": "admin",
      "email": "admin@testflowai.com",
      "avatar": null,
      "status": "active"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 2.2 用户登出

**接口**: `POST /api/auth/logout`

**权限**: 已登录用户

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1774274622604,
  "success": true
}
```

### 2.3 获取当前用户信息

**接口**: `GET /api/auth/me`

**权限**: 已登录用户

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "user-admin-001",
    "username": "admin",
    "email": "admin@testflowai.com",
    "avatar": null,
    "status": "active"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 2.4 修改密码

**接口**: `POST /api/auth/change-password`

**权限**: 已登录用户

**请求参数：**

```json
{
  "oldPassword": "password",
  "newPassword": "NewPass@123456"
}
```

**密码要求：**
- 长度：8-20 位
- 必须包含：大写字母、小写字母、数字、特殊字符

**响应示例：**

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": 1774274622604,
  "success": true
}
```

---

## 3. 用户接口

### 3.1 获取用户列表

**接口**: `GET /api/users`

**权限**: 管理员

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认 1 |
| size | number | 否 | 每页数量，默认 10 |
| status | string | 否 | 用户状态：active/inactive |
| keyword | string | 否 | 搜索关键词 |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 10,
    "page": 1,
    "size": 10,
    "list": [
      {
        "userId": "user-admin-001",
        "username": "admin",
        "email": "admin@testflowai.com",
        "avatar": null,
        "status": "active",
        "createdAt": "2026-03-23T04:57:15.000+00:00",
        "roles": [{"roleId": "role-001", "roleName": "管理员"}]
      }
    ]
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 3.2 获取用户详情

**接口**: `GET /api/users/{userId}`

**权限**: 管理员

**路径参数：**
- `userId`: 用户 ID

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": "user-admin-001",
    "username": "admin",
    "email": "admin@testflowai.com",
    "avatar": "http://localhost:9000/testflowai-avatars/avatar.png",
    "status": "active",
    "roles": [...]
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 3.3 创建用户

**接口**: `POST /api/users`

**权限**: 管理员

**请求参数：**

```json
{
  "username": "newuser",
  "password": "Password@123",
  "email": "user@testflowai.com",
  "roleIds": ["role-002"],
  "status": "active"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "userId": "user-002",
    "username": "newuser",
    "email": "user@testflowai.com",
    "status": "active"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 3.4 更新用户

**接口**: `PUT /api/users/{userId}`

**权限**: 管理员

**路径参数：**
- `userId`: 用户 ID

**请求参数：**

```json
{
  "username": "updateduser",
  "email": "updated@testflowai.com",
  "roleIds": ["role-002"],
  "status": "inactive"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "用户更新成功",
  "data": null,
  "timestamp": 1774274622604,
  "success": true
}
```

### 3.5 删除用户

**接口**: `DELETE /api/users/{userId}`

**权限**: 管理员

**路径参数：**
- `userId`: 用户 ID

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1774274622604,
  "success": true
}
```

---

## 4. 项目接口

### 4.1 获取项目列表

**接口**: `GET /api/projects`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| size | number | 否 | 每页数量 |
| status | string | 否 | 项目状态 |
| keyword | string | 否 | 搜索关键词 |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "page": 1,
    "size": 10,
    "list": [
      {
        "projectId": "proj-001",
        "name": "电商平台测试",
        "description": "电商平台功能测试项目",
        "status": "进行中",
        "progress": 65,
        "createdAt": "2026-03-20T10:00:00.000+00:00",
        "createdBy": "admin"
      }
    ]
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 4.2 获取项目详情

**接口**: `GET /api/projects/{projectId}`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "projectId": "proj-001",
    "name": "电商平台测试",
    "description": "电商平台功能测试项目",
    "status": "进行中",
    "progress": 65,
    "startDate": "2026-03-20",
    "endDate": "2026-04-20",
    "testFlowCount": 15,
    "createdAt": "2026-03-20T10:00:00.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 4.3 创建项目

**接口**: `POST /api/projects`

**权限**: 已登录用户

**请求参数：**

```json
{
  "name": "电商平台测试",
  "description": "电商平台功能测试项目",
  "status": "规划中",
  "progress": 0,
  "startDate": "2026-03-20",
  "endDate": "2026-04-20"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "项目创建成功",
  "data": {
    "projectId": "proj-001",
    "name": "电商平台测试",
    "status": "规划中"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 4.4 更新项目

**接口**: `PUT /api/projects/{projectId}`

**权限**: 已登录用户

**请求参数：**

```json
{
  "name": "电商平台测试",
  "description": "更新后的描述",
  "status": "进行中",
  "progress": 65
}
```

### 4.5 更新项目进度

**接口**: `PATCH /api/projects/{projectId}/progress`

**权限**: 已登录用户

**请求参数：**

```json
{
  "progress": 80
}
```

### 4.6 更新项目状态

**接口**: `PATCH /api/projects/{projectId}/status`

**权限**: 已登录用户

**请求参数：**

```json
{
  "status": "已完成"
}
```

### 4.7 搜索项目

**接口**: `GET /api/projects/search`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| status | string | 否 | 项目状态 |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "projectId": "proj-001",
      "name": "电商平台测试",
      "description": "电商平台功能测试项目"
    }
  ],
  "timestamp": 1774274622604,
  "success": true
}
```

### 4.8 删除项目

**接口**: `DELETE /api/projects/{projectId}`

**权限**: 已登录用户

---

## 5. 测试流接口

### 5.1 获取测试流列表

**接口**: `GET /api/testflows`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "testId": "test-001",
      "title": "用户登录测试",
      "version": "1.0.0",
      "appUrl": "http://localhost:3000",
      "tags": ["登录", "冒烟测试"],
      "projectId": "proj-001",
      "createdAt": "2026-03-20T10:00:00.000+00:00"
    }
  ],
  "timestamp": 1774274622604,
  "success": true
}
```

### 5.2 获取测试流详情

**接口**: `GET /api/testflows/{testId}`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "testId": "test-001",
    "title": "用户登录测试",
    "version": "1.0.0",
    "appUrl": "http://localhost:3000",
    "steps": "[{\"type\":\"navigate\",\"value\":\"http://localhost:3000/login\"}]",
    "variables": "{\"username\":\"admin\",\"password\":\"admin123\"}",
    "tags": "[\"登录\",\"冒烟测试\"]",
    "expectedReport": "{\"status\":\"passed\",\"totalSteps\":5}",
    "projectId": "proj-001",
    "createdAt": "2026-03-20T10:00:00.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 5.3 创建测试流

**接口**: `POST /api/testflows`

**权限**: 已登录用户

**请求参数：**

```json
{
  "title": "用户登录测试",
  "version": "1.0.0",
  "appUrl": "http://localhost:3000",
  "steps": "[{\"type\":\"navigate\",\"value\":\"http://localhost:3000/login\",\"description\":\"打开登录页面\"}]",
  "variables": "{\"username\":\"admin\"}",
  "tags": "[\"登录\",\"冒烟测试\"]",
  "expectedReport": "{\"status\":\"passed\"}",
  "projectId": "proj-001"
}
```

### 5.4 更新测试流

**接口**: `PUT /api/testflows/{testId}`

**权限**: 已登录用户

**请求参数：** 同创建接口

### 5.5 复制测试流

**接口**: `POST /api/testflows/{testId}/duplicate`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "复制成功",
  "data": {
    "testId": "test-002",
    "title": "用户登录测试_副本",
    "version": "1.0.0"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 5.6 导出测试流

**接口**: `GET /api/testflows/{testId}/export`

**权限**: 已登录用户

**响应：** JSON 文件下载

### 5.7 导入测试流

**接口**: `POST /api/testflows/import`

**权限**: 已登录用户

**请求参数：** FormData，包含 `file` 字段（JSON 文件）

### 5.8 搜索测试流

**接口**: `GET /api/testflows/search`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| projectId | string | 否 | 项目 ID |

### 5.9 删除测试流

**接口**: `DELETE /api/testflows/{testId}`

**权限**: 已登录用户

---

## 6. 执行接口

### 6.1 创建执行记录

**接口**: `POST /api/executions`

**权限**: 已登录用户

**请求参数：**

```json
{
  "testId": "test-001",
  "mode": "manual"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "executionId": "exec-001",
    "testId": "test-001",
    "mode": "manual",
    "status": "pending",
    "createdAt": "2026-03-23T10:00:00.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 6.2 开始执行

**接口**: `POST /api/executions/{executionId}/start`

**权限**: 已登录用户

### 6.3 执行测试流

**接口**: `POST /api/executions/{executionId}/run`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "executionId": "exec-001",
    "status": "passed",
    "totalSteps": 5,
    "passedSteps": 5,
    "failedSteps": 0,
    "startTime": "2026-03-23T10:00:00.000+00:00",
    "endTime": "2026-03-23T10:00:10.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 6.4 停止执行

**接口**: `POST /api/executions/{executionId}/stop`

**权限**: 已登录用户

### 6.5 获取执行列表

**接口**: `GET /api/executions`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| size | number | 否 | 每页数量 |
| status | string | 否 | 执行状态 |
| testId | string | 否 | 测试流 ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 50,
    "page": 1,
    "size": 10,
    "list": [
      {
        "executionId": "exec-001",
        "testId": "test-001",
        "mode": "manual",
        "status": "passed",
        "totalSteps": 5,
        "passedSteps": 5,
        "failedSteps": 0,
        "startTime": "2026-03-23T10:00:00.000+00:00",
        "endTime": "2026-03-23T10:00:10.000+00:00"
      }
    ]
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 6.6 获取执行详情

**接口**: `GET /api/executions/{executionId}`

**权限**: 已登录用户

### 6.7 获取测试流执行历史

**接口**: `GET /api/executions/test/{testId}`

**权限**: 已登录用户

### 6.8 获取步骤结果

**接口**: `GET /api/executions/{executionId}/steps`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "resultId": "step-001",
      "stepIndex": 0,
      "type": "navigate",
      "status": "passed",
      "startTime": "2026-03-23T10:00:00.000+00:00",
      "endTime": "2026-03-23T10:00:02.000+00:00",
      "log": "页面加载成功",
      "screenshot": null
    }
  ],
  "timestamp": 1774274622604,
  "success": true
}
```

### 6.9 获取状态统计

**接口**: `GET /api/executions/stats/status`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pending": 5,
    "running": 2,
    "passed": 100,
    "failed": 10,
    "stopped": 3
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 6.10 获取最近执行记录

**接口**: `GET /api/executions/recent`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| limit | number | 否 | 10 | 数量限制 |

### 6.11 删除执行记录

**接口**: `DELETE /api/executions/{executionId}`

**权限**: 已登录用户

---

## 7. 报告接口

### 7.1 生成报告

**接口**: `POST /api/reports/generate/{executionId}`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "reportId": "report-001",
    "executionId": "exec-001",
    "status": "passed",
    "generatedAt": "2026-03-23T10:00:15.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 7.2 获取报告列表

**接口**: `GET /api/reports`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| size | number | 否 | 每页数量 |

### 7.3 获取报告详情

**接口**: `GET /api/reports/{reportId}`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "reportId": "report-001",
    "executionId": "exec-001",
    "testId": "test-001",
    "status": "passed",
    "totalSteps": 5,
    "passedSteps": 5,
    "failedSteps": 0,
    "passRate": 100,
    "duration": 10000,
    "steps": [...],
    "generatedAt": "2026-03-23T10:00:15.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 7.4 获取报告 JSON 数据

**接口**: `GET /api/reports/{reportId}/data`

**权限**: 已登录用户

### 7.5 下载报告

**接口**: `GET /api/reports/{reportId}/download`

**权限**: 已登录用户

**响应：** JSON 文件下载

### 7.6 根据执行 ID 获取报告

**接口**: `GET /api/reports/execution/{executionId}`

**权限**: 已登录用户

### 7.7 根据测试流 ID 获取报告列表

**接口**: `GET /api/reports/test/{testId}`

**权限**: 已登录用户

### 7.8 删除报告

**接口**: `DELETE /api/reports/{reportId}`

**权限**: 已登录用户

---

## 8. 定时任务接口

### 8.1 创建定时任务

**接口**: `POST /api/scheduled-tasks`

**权限**: 已登录用户

**请求参数：**

```json
{
  "taskName": "daily_smoke_test",
  "testId": "test-001",
  "cronExpression": "0 9 * * 1-5",
  "title": "每日冒烟测试",
  "description": "工作日早上 9 点执行冒烟测试",
  "status": "active"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "taskId": "task-001",
    "taskName": "daily_smoke_test",
    "testId": "test-001",
    "title": "每日冒烟测试",
    "cronExpression": "0 9 * * 1-5",
    "status": "active",
    "nextExecutionAt": "2026-03-24T09:00:00.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 8.2 更新定时任务

**接口**: `PUT /api/scheduled-tasks/{taskId}`

**权限**: 已登录用户

**请求参数：**

```json
{
  "taskName": "daily_smoke_test",
  "cronExpression": "0 10 * * 1-5",
  "title": "每日冒烟测试（更新）",
  "description": "工作日早上 10 点执行"
}
```

### 8.3 切换任务状态

**接口**: `PATCH /api/scheduled-tasks/{taskId}/status`

**权限**: 已登录用户

**请求参数：**

```json
{
  "status": "inactive"
}
```

### 8.4 手动触发任务

**接口**: `POST /api/scheduled-tasks/{taskId}/trigger`

**权限**: 已登录用户

**响应示例：**

```json
{
  "code": 200,
  "message": "任务已触发",
  "data": null,
  "timestamp": 1774274622604,
  "success": true
}
```

### 8.5 获取任务列表

**接口**: `GET /api/scheduled-tasks`

**权限**: 已登录用户

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码 |
| size | number | 否 | 每页数量 |
| status | string | 否 | 任务状态 |

### 8.6 获取任务详情

**接口**: `GET /api/scheduled-tasks/{taskId}`

**权限**: 已登录用户

### 8.7 根据测试流 ID 获取任务列表

**接口**: `GET /api/scheduled-tasks/test/{testId}`

**权限**: 已登录用户

### 8.8 删除定时任务

**接口**: `DELETE /api/scheduled-tasks/{taskId}`

**权限**: 已登录用户

---

## 9. 统计接口

### 9.1 获取仪表盘概述统计

**接口**: `GET /api/stats/overview`

**权限**: 公开

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "projectCount": 10,
    "testFlowCount": 50,
    "executionCount": 200,
    "scheduledTaskCount": 5,
    "passRate": 95.5
  },
  "timestamp": 1774274622604,
  "success": true
}
```

### 9.2 获取执行状态统计

**接口**: `GET /api/stats/execution/status`

**权限**: 公开

### 9.3 获取项目统计

**接口**: `GET /api/stats/projects`

**权限**: 公开

### 9.4 获取测试用例统计

**接口**: `GET /api/stats/testflows`

**权限**: 公开

### 9.5 获取定时任务统计

**接口**: `GET /api/stats/scheduled-tasks`

**权限**: 公开

---

## 10. 文件上传接口

### 10.1 上传头像

**接口**: `POST /api/files/avatar`

**权限**: 已登录用户

**请求类型**: `multipart/form-data`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 头像文件（JPG/PNG，最大 5MB） |

**响应示例：**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "avatarUrl": "http://localhost:9000/testflowai-avatars/avatar-001.png"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

---

## 11. 系统接口

### 11.1 健康检查

**接口**: `GET /api/health`

**权限**: 公开

**响应示例：**

```json
{
  "code": 200,
  "message": "服务正常",
  "data": {
    "status": "UP",
    "timestamp": "2026-03-23T10:00:00.000+00:00"
  },
  "timestamp": 1774274622604,
  "success": true
}
```

---

## 12. 错误码说明

### 12.1 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 12.2 业务错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或 Token 过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 12.3 错误响应示例

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null,
  "timestamp": 1774274622604,
  "success": false
}
```

```json
{
  "code": 403,
  "message": "没有权限访问该资源",
  "data": null,
  "timestamp": 1774274622604,
  "success": false
}
```

```json
{
  "code": 404,
  "message": "测试流不存在",
  "data": null,
  "timestamp": 1774274622604,
  "success": false
}
```

---

**文档版本**: 1.0
**最后更新**: 2026-03-23
**维护者**: TestFlowAI Team

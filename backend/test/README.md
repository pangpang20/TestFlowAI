# TestFlowAI 后端接口测试

## 目录结构

```
test/
├── README.md           # 测试文档（本文件）
├── test_api.sh         # API 接口功能测试脚本
├── test_data/          # 测试数据文件（待添加）
└── reports/            # 测试报告输出目录（待添加）
```

## 快速开始

### 前置条件

1. 后端服务已启动（默认端口 18080）
2. MySQL 数据库已初始化
3. 默认管理员账号：`admin` / `admin123`

### 运行测试

```bash
# 进入测试目录
cd /opt/TestFlowAI/backend/test

# 运行完整测试
./test_api.sh

# 或者从项目根目录运行
./test/test_api.sh
```

### 测试结果

测试完成后会显示：
- 通过的测试数量（绿色）
- 失败的测试数量（红色）
- 总计测试数量

## 测试覆盖范围

### 认证模块
- [x] 登录成功
- [x] 登录失败（密码错误）
- [x] 登录失败（用户不存在）

### 用户管理
- [x] 创建用户
- [x] 获取用户列表
- [x] 获取用户详情
- [x] 更新用户
- [x] 创建重复用户名用户（应该失败）
- [ ] 删除用户（待添加）

### 角色管理
- [x] 创建角色
- [x] 获取角色列表
- [x] 获取角色详情
- [x] 更新角色
- [ ] 删除角色（待添加）

### 权限管理
- [x] 创建权限
- [x] 获取权限列表
- [x] 获取权限详情
- [x] 更新权限
- [ ] 删除权限（待添加）

### 分配功能
- [x] 给用户分配角色
- [x] 获取用户的角色
- [x] 给角色分配权限
- [x] 获取角色的权限

### 项目管理
- [x] 创建项目（日期验证）
- [x] 获取项目列表
- [ ] 更新项目（待添加）
- [ ] 删除项目（待添加）

## 添加新测试

在 `test_api.sh` 中添加新的测试用例，遵循以下格式：

```bash
# 章节标题
print_header "模块名称测试"

# 单个测试
response=$(curl -s -X POST "$BASE_URL/api/xxx" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"key":"value"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "测试描述")
```

## 清理测试数据

测试脚本会自动清理创建的测试数据。如果需要手动清理：

```bash
# 脚本末尾的 cleanup 函数会在测试完成后执行
# 或手动调用
source test_api.sh
cleanup
```

## 输出说明

- `✓ PASS` - 测试通过（绿色）
- `✗ FAIL` - 测试失败（红色）
- `=== 标题 ===` - 章节标题（黄色）

## 故障排查

### 后端服务未启动
```bash
# 检查后端进程
ps aux | grep backend-1.0.0-SNAPSHOT.jar

# 启动后端
cd /opt/TestFlowAI/backend
nohup java -jar target/backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev > /tmp/backend.log 2>&1 &
```

### 数据库连接失败
```bash
# 检查 MySQL 容器
docker ps | grep mysql

# 重启 MySQL
docker restart mysql
```

### Token 获取失败
确认管理员账号密码正确：
- 用户名：`admin`
- 密码：`admin123`

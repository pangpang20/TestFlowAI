#!/bin/bash
# TestFlowAI 后端接口功能测试脚本
# 用途：测试后端 API 的基本功能

# set -e 已移除，以便所有测试都能运行完成

BASE_URL="http://localhost:18080"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 统计
TESTS_PASSED=0
TESTS_FAILED=0

# 打印测试结果
print_result() {
    if [ "$1" -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
        ((TESTS_FAILED++))
    fi
}

# 打印章节标题
print_header() {
    echo -e "\n${YELLOW}=== $1 ===${NC}"
}

# 获取 Token
get_token() {
    local response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}')

    TOKEN=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}无法获取 Token，登录失败${NC}"
        exit 1
    fi
    echo "Token 获取成功"
}

# 清理测试数据
cleanup() {
    echo -e "\n${YELLOW}=== 清理测试数据 ===${NC}"
    # 删除测试用户
    local user_id=$(curl -s -X GET "$BASE_URL/api/users" \
        -H "Authorization: Bearer $TOKEN" | \
        grep -o '"userId":"[^"]*","username":"testuser"' | cut -d'"' -f4)
    if [ -n "$user_id" ]; then
        curl -s -X DELETE "$BASE_URL/api/users/$user_id" \
            -H "Authorization: Bearer $TOKEN" > /dev/null
        echo "✓ 删除测试用户"
    fi

    # 删除测试角色
    local role_id=$(curl -s -X GET "$BASE_URL/api/roles" \
        -H "Authorization: Bearer $TOKEN" | \
        grep -o '"roleId":"[^"]*","roleName":"test_role"' | cut -d'"' -f4)
    if [ -n "$role_id" ]; then
        curl -s -X DELETE "$BASE_URL/api/roles/$role_id" \
            -H "Authorization: Bearer $TOKEN" > /dev/null
        echo "✓ 删除测试角色"
    fi

    # 删除测试权限
    local perm_id=$(curl -s -X GET "$BASE_URL/api/permissions" \
        -H "Authorization: Bearer $TOKEN" | \
        grep -o '"permissionId":"[^"]*","permissionCode":"test:read"' | cut -d'"' -f4)
    if [ -n "$perm_id" ]; then
        curl -s -X DELETE "$BASE_URL/api/permissions/$perm_id" \
            -H "Authorization: Bearer $TOKEN" > /dev/null
        echo "✓ 删除测试权限"
    fi

    # 删除测试项目
    local proj_id=$(curl -s -X GET "$BASE_URL/api/projects" \
        -H "Authorization: Bearer $TOKEN" | \
        grep -o '"projectId":"[^"]*","projectName":"测试项目"' | cut -d'"' -f4)
    if [ -n "$proj_id" ]; then
        curl -s -X DELETE "$BASE_URL/api/projects/$proj_id" \
            -H "Authorization: Bearer $TOKEN" > /dev/null
        echo "✓ 删除测试项目"
    fi
}

# ========== 开始测试 ==========

print_header "准备测试环境"

# 检查后端是否启动
if ! curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/auth/login" | grep -q "200\|400\|401\|500"; then
    echo -e "${RED}后端服务未启动或无法访问${NC}"
    exit 1
fi
echo "✓ 后端服务可访问"

# 获取 Token
get_token

# ========== 认证模块测试 ==========
print_header "认证模块测试"

# 测试登录 - 成功
response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "登录成功"

# 测试登录 - 失败（密码错误）
response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"wrongpassword"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "401" ] && echo 0 || echo 1) "登录失败（密码错误）"

# 测试登录 - 失败（用户名不存在）
response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"nonexistent","password":"admin123"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "401" ] && echo 0 || echo 1) "登录失败（用户不存在）"

# ========== 用户管理测试 ==========
print_header "用户管理测试"

# 创建用户
response=$(curl -s -X POST "$BASE_URL/api/users" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"username":"testuser","password":"Test1234!@","email":"test@test.com"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "创建用户"

# 获取用户列表
response=$(curl -s -X GET "$BASE_URL/api/users" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取用户列表"

# 获取用户详情
user_id=$(echo "$response" | grep -o '"userId":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -n "$user_id" ]; then
    response=$(curl -s -X GET "$BASE_URL/api/users/$user_id" \
        -H "Authorization: Bearer $TOKEN")
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取用户详情"
fi

# 更新用户
if [ -n "$user_id" ]; then
    response=$(curl -s -X PUT "$BASE_URL/api/users/$user_id" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{"username":"testuser","email":"testupdated@test.com","status":1}')
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "更新用户"
fi

# 创建重复用户名的用户
response=$(curl -s -X POST "$BASE_URL/api/users" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"username":"admin","password":"Test1234!@","email":"test2@test.com"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "400" ] && echo 0 || echo 1) "创建重复用户名用户（应该失败）"

# ========== 角色管理测试 ==========
print_header "角色管理测试"

# 创建角色
response=$(curl -s -X POST "$BASE_URL/api/roles" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"roleName":"test_role","displayName":"测试角色","description":"用于测试的角色"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "创建角色"

# 获取角色列表
response=$(curl -s -X GET "$BASE_URL/api/roles" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取角色列表"

# 获取角色详情
role_id=$(echo "$response" | grep -o '"roleId":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -n "$role_id" ]; then
    response=$(curl -s -X GET "$BASE_URL/api/roles/$role_id" \
        -H "Authorization: Bearer $TOKEN")
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取角色详情"
fi

# 更新角色
if [ -n "$role_id" ]; then
    response=$(curl -s -X PUT "$BASE_URL/api/roles/$role_id" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{"roleName":"test_role","displayName":"更新后的测试角色","description":"更新描述"}')
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "更新角色"
fi

# ========== 权限管理测试 ==========
print_header "权限管理测试"

# 创建权限
response=$(curl -s -X POST "$BASE_URL/api/permissions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"permissionCode":"test:read","permissionName":"测试查看","resource":"test","action":"read","description":"测试权限"}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "创建权限"

# 获取权限列表
response=$(curl -s -X GET "$BASE_URL/api/permissions" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取权限列表"

# 获取权限详情
perm_id=$(echo "$response" | grep -o '"permissionId":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -n "$perm_id" ]; then
    response=$(curl -s -X GET "$BASE_URL/api/permissions/$perm_id" \
        -H "Authorization: Bearer $TOKEN")
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取权限详情"
fi

# 更新权限
if [ -n "$perm_id" ]; then
    response=$(curl -s -X PUT "$BASE_URL/api/permissions/$perm_id" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{"permissionCode":"test:read","permissionName":"测试查看更新","resource":"test","action":"read","description":"更新描述"}')
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    print_result $([ "$code" = "200" ] && echo 0 || echo 1) "更新权限"
fi

# ========== 分配角色和权限测试 ==========
print_header "分配功能测试"

# 获取测试数据
user_id=$(curl -s -X GET "$BASE_URL/api/users" \
    -H "Authorization: Bearer $TOKEN" | \
    grep -o '"userId":"[^"]*"' | head -1 | cut -d'"' -f4)
role_id=$(curl -s -X GET "$BASE_URL/api/roles" \
    -H "Authorization: Bearer $TOKEN" | \
    grep -o '"roleId":"[^"]*"' | head -1 | cut -d'"' -f4)
perm_id=$(curl -s -X GET "$BASE_URL/api/permissions" \
    -H "Authorization: Bearer $TOKEN" | \
    grep -o '"permissionId":"[^"]*"' | head -1 | cut -d'"' -f4)

# 给用户分配角色
response=$(curl -s -X POST "$BASE_URL/api/users/$user_id/roles" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"roleIds\":[\"$role_id\"]}")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "给用户分配角色"

# 获取用户的角色
response=$(curl -s -X GET "$BASE_URL/api/users/$user_id/roles" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取用户的角色"

# 给角色分配权限
response=$(curl -s -X POST "$BASE_URL/api/roles/$role_id/permissions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"permissionIds\":[\"$perm_id\"]}")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "给角色分配权限"

# 获取角色的权限
response=$(curl -s -X GET "$BASE_URL/api/roles/$role_id/permissions" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取角色的权限"

# ========== 项目管理测试 ==========
print_header "项目管理测试"

# 创建项目 - 日期验证失败
response=$(curl -s -X POST "$BASE_URL/api/projects" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"projectName":"测试项目","description":"测试","owner":"admin","status":"active","startDate":"2026-03-25","endDate":"2026-03-20","progress":0}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
message=$(echo "$response" | grep -o '"message":"[^"]*"' | cut -d'"' -f4)
print_result $([ "$code" = "500" ] && [[ "$message" == *"结束日期必须晚于开始日期"* ]] && echo 0 || echo 1) "项目日期验证（结束早于开始）"

# 创建项目 - 正常
response=$(curl -s -X POST "$BASE_URL/api/projects" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"projectName":"测试项目","description":"测试","owner":"admin","status":"active","startDate":"2026-03-20","endDate":"2026-03-25","progress":0}')
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "创建项目"

# 获取项目列表
response=$(curl -s -X GET "$BASE_URL/api/projects" \
    -H "Authorization: Bearer $TOKEN")
code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
print_result $([ "$code" = "200" ] && echo 0 || echo 1) "获取项目列表"

# ========== 测试总结 ==========
echo -e "\n${YELLOW}=== 测试总结 ===${NC}"
echo -e "通过：${GREEN}$TESTS_PASSED${NC}"
echo -e "失败：${RED}$TESTS_FAILED${NC}"
echo "总计：$((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}所有测试通过！${NC}"
    exit 0
else
    echo -e "\n${RED}部分测试失败！${NC}"
    exit 1
fi

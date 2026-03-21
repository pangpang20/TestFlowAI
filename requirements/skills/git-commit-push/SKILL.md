---
name: git-commit-push
description: 分析代码变更生成 commit 信息，执行 git add、commit 和 push。当用户要求提交代码、推送更改、生成 commit 信息或执行 /commit 时使用。
---

# Git Commit & Push

自动分析变更内容，生成 commit 信息，提交并推送到远程。

## 工作流程

### 1. 查看变更状态

```bash
git status
git diff --stat
```

### 2. 分析变更内容

查看具体改动：

```bash
git diff          # 未暂存的更改
git diff --cached # 已暂存的更改
```

### 3. 生成 Commit 信息

根据变更类型自动选择格式：

| 变更类型 | 前缀       | 示例                                  |
| -------- | ---------- | ------------------------------------- |
| 新功能   | `feat`     | feat(widget): add session persistence |
| 修复 Bug | `fix`      | fix(api): correct response handling   |
| 重构     | `refactor` | refactor(service): simplify logic     |
| 样式调整 | `style`    | style(css): update button colors      |
| 文档     | `docs`     | docs(readme): update installation     |
| 测试     | `test`     | test(unit): add user service tests    |
| 构建     | `build`    | build(deps): upgrade spring boot      |
| 杂项     | `chore`    | chore: update gitignore               |

**格式规则：**
- 第一行：`type(scope): 简短描述`（不超过 72 字符）
- 空一行
- 正文：详细说明改动内容（可选）

### 4. 执行提交

```bash
# 暂存所有更改
git add -A

# 提交（使用生成的信息）
git commit -m "commit message"

# 推送到当前分支
git push
```

## 示例

**场景：修复了小助手会话刷新丢失问题**

```bash
git add -A
git commit -m "feat(widget): add session persistence on page refresh

- Store sessionId in localStorage by agentId
- Load history messages when session restored
- Verify session validity before restoring"
git push
```

**场景：修复了 API 路径错误**

```bash
git add -A
git commit -m "fix(api): correct endpoint path for preset questions"
git push
```

## 注意事项

- 推送前确认当前分支：`git branch --show-current`
- 如有冲突需先拉取：`git pull --rebase`
- 敏感信息不要提交（密码、密钥等）

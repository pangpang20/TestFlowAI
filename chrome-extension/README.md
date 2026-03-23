# TestFlowAI Chrome 扩展录制器

## 功能概述

Chrome 扩展录制器用于捕获用户在浏览器中的操作，并自动转换为 TestFlowAI 测试用例。

## 安装步骤

1. 打开 Chrome 浏览器，进入扩展管理页面：`chrome://extensions/`
2. 右上角打开"开发者模式"
3. 点击"加载已解压的扩展程序"
4. 选择 `/opt/TestFlowAI/chrome-extension` 目录
5. 扩展安装完成，图标出现在浏览器工具栏

## 使用方法

### 开始录制

1. 点击扩展图标打开 popup
2. 配置 TestFlowAI 地址（默认：http://localhost:18080）
3. 点击"开始录制"按钮
4. 页面右上角出现红色"录制中"指示器

### 录制操作

录制器自动捕获以下操作：
- 点击（click）
- 双击（dblclick）
- 输入（input）
- 选择（select）
- 勾选/取消勾选（check/uncheck）
- 页面导航（navigate）
- 滚动（scroll）
- 按键（keypress）

### 停止录制

1. 点击扩展图标
2. 点击"停止录制"按钮
3. 查看已录制的操作列表

### 保存测试用例

1. 点击"保存到 TestFlowAI"
2. 输入测试流名称
3. 如果需要，输入用户名和密码登录
4. 保存成功后，可在 TestFlowAI 后台查看

## 技术实现

### 核心文件

| 文件 | 说明 |
|------|------|
| manifest.json | 扩展配置文件 |
| background.js | 后台脚本，管理录制状态 |
| content.js | 页面脚本，捕获用户操作 |
| recorder.js | 录制器核心逻辑 |
| popup.html | popup 界面 |
| popup.js | popup 交互逻辑 |

### 录制的动作类型

```javascript
{
  type: 'click',        // 点击
  type: 'dblclick',     // 双击
  type: 'input',        // 输入
  type: 'select',       // 选择
  type: 'check',        // 勾选
  type: 'uncheck',      // 取消勾选
  type: 'navigate',     // 导航
  type: 'scroll',       // 滚动
  type: 'hover',        // 悬停
  type: 'keypress'      // 按键
}
```

### 动作数据结构

```javascript
{
  type: 'click',
  selector: '#login-btn',
  description: '点击：登录按钮',
  tagName: 'button',
  text: '登录',
  x: 100,
  y: 200,
  timestamp: 1000,
  screenshot: true
}
```

### 选择器生成策略

1. 优先使用元素 ID（最稳定）
2. 使用类名 + 标签名
3. 添加 nth-child 区分同级元素
4. 向上遍历父元素构建完整路径

## 后端 API

### 保存测试流

```
POST /api/testflows
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "录制的测试",
  "version": "1.0.0",
  "appUrl": "http://example.com",
  "steps": "[{\"type\":\"click\",\"selector\":\"#btn\"}]",
  "variables": "{}",
  "tags": "[\"recorded\"]"
}
```

## 注意事项

1. 录制前确保 TestFlowAI 后端服务已启动
2. 第一次保存需要输入账号密码登录
3. Token 会保存在 Chrome 本地存储中
4. 敏感页面（如银行网站）建议不要录制
5. 录制时避免过快操作，可能导致捕获不完整

## 故障排除

### 无法开始录制
- 检查扩展是否已加载
- 刷新页面后重试
- 查看 Chrome 控制台错误信息

### 保存失败
- 检查 TestFlowAI 地址是否正确
- 确认后端服务运行正常
- 检查网络连接

### 操作捕获不完整
- 确保页面完全加载后再开始录制
- 避免在 iframe 内操作（目前不支持）
- 某些动态生成的元素可能无法准确定位

// TestFlowAI Recorder - Popup Script
// 处理 popup 交互逻辑

let isRecording = false;
let actions = [];
let currentTabId = null;

// 配置
let config = {
  apiUrl: 'http://localhost:18080',
  token: ''
};

// DOM 元素
const statusEl = document.getElementById('status');
const statusTextEl = document.getElementById('statusText');
const toggleBtn = document.getElementById('toggleBtn');
const toggleIcon = document.getElementById('toggleIcon');
const toggleText = document.getElementById('toggleText');
const actionListEl = document.getElementById('actionList');
const actionButtonsEl = document.getElementById('actionButtons');
const clearBtn = document.getElementById('clearBtn');
const saveBtn = document.getElementById('saveBtn');
const syncStatusEl = document.getElementById('syncStatus');
const apiUrlInput = document.getElementById('apiUrl');

// 初始化
async function init() {
  // 加载配置
  await loadConfig();

  // 获取当前标签页
  const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
  if (tabs[0]) {
    currentTabId = tabs[0].id;
  }

  // 获取录制状态
  updateStatus();

  // 绑定事件
  toggleBtn.addEventListener('click', toggleRecording);
  clearBtn.addEventListener('click', clearActions);
  saveBtn.addEventListener('click', saveToTestFlowAI);
  apiUrlInput.addEventListener('change', saveConfig);

  // 监听来自 background 的消息
  chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'ACTIONS_UPDATED') {
      actions = message.actions;
      renderActions();
    }
    sendResponse({ success: true });
    return true;
  });
}

// 加载配置
async function loadConfig() {
  try {
    const result = await chrome.storage.local.get(['apiUrl', 'token']);
    if (result.apiUrl) {
      config.apiUrl = result.apiUrl;
      apiUrlInput.value = result.apiUrl;
    }
    if (result.token) {
      config.token = result.token;
    }
  } catch (err) {
    console.error('加载配置失败:', err);
  }
}

// 保存配置
async function saveConfig() {
  config.apiUrl = apiUrlInput.value.trim();
  try {
    await chrome.storage.local.set({ apiUrl: config.apiUrl });
    showSyncStatus('配置已保存', 'success');
  } catch (err) {
    console.error('保存配置失败:', err);
  }
}

// 更新状态显示
async function updateStatus() {
  try {
    const response = await chrome.runtime.sendMessage({ type: 'GET_RECORDING_STATUS' });
    isRecording = response?.isRecording || false;
    actions = response?.actions || [];
    updateUI();
  } catch (err) {
    console.error('获取状态失败:', err);
    isRecording = false;
    updateUI();
  }
}

// 切换录制状态
async function toggleRecording() {
  if (isRecording) {
    await stopRecording();
  } else {
    await startRecording();
  }
}

// 开始录制
async function startRecording() {
  try {
    const response = await chrome.runtime.sendMessage({
      type: 'START_RECORDING',
      tabId: currentTabId
    });

    if (response?.success) {
      isRecording = true;
      updateUI();
    }
  } catch (err) {
    console.error('开始录制失败:', err);
    alert('开始录制失败，请刷新页面后重试');
  }
}

// 停止录制
async function stopRecording() {
  try {
    const response = await chrome.runtime.sendMessage({ type: 'STOP_RECORDING' });

    if (response?.success) {
      isRecording = false;
      actions = response.actions || [];
      updateUI();
    }
  } catch (err) {
    console.error('停止录制失败:', err);
  }
}

// 更新 UI
function updateUI() {
  if (isRecording) {
    statusEl.className = 'status recording';
    statusTextEl.textContent = '正在录制...';
    toggleIcon.textContent = '⏹';
    toggleText.textContent = '停止录制';
    toggleBtn.className = 'btn btn-danger';
    actionButtonsEl.style.display = 'flex';
  } else {
    statusEl.className = 'status stopped';
    statusTextEl.textContent = '已停止录制';
    toggleIcon.textContent = '▶';
    toggleText.textContent = '开始录制';
    toggleBtn.className = 'btn btn-primary';
    if (actions.length === 0) {
      actionButtonsEl.style.display = 'none';
    }
  }

  renderActions();
}

// 渲染动作列表
function renderActions() {
  if (actions.length === 0) {
    actionListEl.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">📝</div>
        <p>暂无录制操作</p>
        <p style="font-size: 11px; margin-top: 8px;">点击"开始录制"按钮开始记录</p>
      </div>
    `;
    return;
  }

  actionListEl.innerHTML = actions.map(action => `
    <div class="action-item">
      <div class="action-icon ${action.type}">
        ${getActionIcon(action.type)}
      </div>
      <div class="action-content">
        <div class="action-type">${action.type}</div>
        <div class="action-desc">${escapeHtml(action.description || '')}</div>
        <div class="action-time">${formatTime(action.timestamp)}</div>
      </div>
    </div>
  `).join('');
}

// 获取动作图标
function getActionIcon(type) {
  const icons = {
    click: '👆',
    dblclick: '👆👆',
    input: '⌨️',
    select: '📋',
    check: '☑️',
    uncheck: '☐',
    navigate: '🔗',
    scroll: '📜',
    hover: '🖱️',
    keypress: '⌨️'
  };
  return icons[type] || '•';
}

// 清空动作
async function clearActions() {
  try {
    await chrome.runtime.sendMessage({ type: 'CLEAR_ACTIONS' });
    actions = [];
    renderActions();
  } catch (err) {
    console.error('清空动作失败:', err);
  }
}

// 保存到 TestFlowAI
async function saveToTestFlowAI() {
  if (actions.length === 0) {
    alert('没有可保存的录制操作');
    return;
  }

  const title = prompt('请输入测试流名称:', `录制的测试-${new Date().toLocaleTimeString()}`);
  if (!title) return;

  saveBtn.disabled = true;
  saveBtn.textContent = '保存中...';

  try {
    // 首先获取 token
    const token = await getStoredToken();
    if (!token) {
      // 尝试从 popup 获取或者提示用户登录
      const loginInfo = await promptForLogin();
      if (!loginInfo) {
        throw new Error('需要登录才能保存');
      }
      config.token = loginInfo.token;
    }

    // 转换动作为 TestFlow 格式
    const testFlowData = convertToTestFlow(actions, title);

    // 发送到后端 API
    const response = await fetch(`${config.apiUrl}/api/testflows`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${config.token}`
      },
      body: JSON.stringify(testFlowData)
    });

    const result = await response.json();

    if (response.ok && result.success) {
      showSyncStatus('✓ 保存成功！', 'success');
      // 保存后停止录制
      await stopRecording();
    } else {
      throw new Error(result.message || '保存失败');
    }
  } catch (err) {
    console.error('保存失败:', err);
    showSyncStatus(`✗ ${err.message}`, 'error');
  } finally {
    saveBtn.disabled = false;
    saveBtn.textContent = '保存到 TestFlowAI';
  }
}

// 转换动作为 TestFlow 格式
function convertToTestFlow(actions, title) {
  const steps = actions.map((action, index) => ({
    id: index + 1,
    type: mapActionType(action.type),
    description: action.description || `${action.type} operation`,
    selector: action.selector,
    value: action.value,
    expected: action.expected,
    screenshot: action.screenshot || false
  }));

  return {
    title: title,
    version: '1.0.0',
    appUrl: window.location.origin,
    steps: JSON.stringify(steps, null, 2),
    variables: JSON.stringify({}, null, 2),
    tags: JSON.stringify(['recorded', new Date().toLocaleDateString()], null, 2)
  };
}

// 映射动作类型
function mapActionType(type) {
  const typeMap = {
    click: 'click',
    dblclick: 'doubleClick',
    input: 'input',
    select: 'select',
    check: 'check',
    uncheck: 'uncheck',
    navigate: 'navigate',
    scroll: 'scroll',
    hover: 'hover',
    keypress: 'keypress'
  };
  return typeMap[type] || type;
}

// 获取存储的 token
async function getStoredToken() {
  try {
    const result = await chrome.storage.local.get(['token']);
    return result.token;
  } catch (err) {
    console.error('获取 token 失败:', err);
    return null;
  }
}

// 提示用户登录
async function promptForLogin() {
  return new Promise((resolve) => {
    const username = prompt('请输入 TestFlowAI 用户名:');
    if (!username) {
      resolve(null);
      return;
    }

    const password = prompt('请输入 TestFlowAI 密码:');
    if (!password) {
      resolve(null);
      return;
    }

    // 调用登录 API
    fetch(`${config.apiUrl}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    })
    .then(res => res.json())
    .then(data => {
      if (data.success && data.data?.token) {
        chrome.storage.local.set({ token: data.data.token });
        config.token = data.data.token;
        resolve({ token: data.data.token });
      } else {
        resolve(null);
      }
    })
    .catch(() => resolve(null));
  });
}

// 显示同步状态
function showSyncStatus(message, type) {
  syncStatusEl.textContent = message;
  syncStatusEl.className = `sync-status ${type}`;
  setTimeout(() => {
    syncStatusEl.className = 'sync-status hidden';
  }, 3000);
}

// 格式化时间
function formatTime(ms) {
  if (!ms) return '';
  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  if (minutes > 0) {
    return `${minutes}分${seconds % 60}秒`;
  }
  return `${seconds}秒`;
}

// HTML 转义
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// 启动
init();

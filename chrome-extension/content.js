// TestFlowAI Recorder - Content Script
// 注入到页面中，捕获用户操作

let isRecording = false;
let tabId = null;

// 忽略的元素和事件
const IGNORED_TAGS = ['SCRIPT', 'STYLE', 'LINK', 'META', 'NOSCRIPT'];
const IGNORED_EVENTS = ['mouseover', 'mouseout', 'mouseenter', 'mouseleave'];

// 初始化
function init() {
  console.log('TestFlowAI Content Script 已加载');

  // 监听来自 background 的消息
  chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    console.log('Content Script 收到消息:', message);

    switch (message.type) {
      case 'START_RECORDING':
        startRecording(message.tabId);
        sendResponse({ success: true });
        break;

      case 'STOP_RECORDING':
        stopRecording();
        sendResponse({ success: true });
        break;

      case 'PAGE_LOADED':
        console.log('页面已加载，tabId:', message.tabId);
        break;

      default:
        sendResponse({ success: false });
    }

    return true;
  });

  // 通知 background script 已准备好
  chrome.runtime.sendMessage({ type: 'CONTENT_SCRIPT_READY' }).catch(() => {});
}

// 开始录制
function startRecording(id) {
  if (isRecording) return;

  isRecording = true;
  tabId = id;

  console.log('开始录制');

  // 添加事件监听器
  addEventListeners();

  // 显示录制提示
  showRecordingIndicator();
}

// 停止录制
function stopRecording() {
  isRecording = false;

  console.log('停止录制');

  // 移除事件监听器
  removeEventListeners();

  // 隐藏录制提示
  hideRecordingIndicator();
}

// 添加事件监听器
function addEventListeners() {
  document.addEventListener('click', handleClick, true);
  document.addEventListener('dblclick', handleDblClick, true);
  document.addEventListener('input', handleInput, true);
  document.addEventListener('change', handleChange, true);
  document.addEventListener('keydown', handleKeyDown, true);
  document.addEventListener('focus', handleFocus, true);
  document.addEventListener('scroll', handleScroll, true);
  window.addEventListener('beforeunload', handleBeforeUnload);
}

// 移除事件监听器
function removeEventListeners() {
  document.removeEventListener('click', handleClick, true);
  document.removeEventListener('dblclick', handleDblClick, true);
  document.removeEventListener('input', handleInput, true);
  document.removeEventListener('change', handleChange, true);
  document.removeEventListener('keydown', handleKeyDown, true);
  document.removeEventListener('focus', handleFocus, true);
  document.removeEventListener('scroll', handleScroll, true);
  window.removeEventListener('beforeunload', handleBeforeUnload);
}

// 获取元素的 CSS 选择器
function getSelector(element) {
  if (!element) return null;

  // 如果有 ID，使用 ID 选择器
  if (element.id) {
    return `#${element.id}`;
  }

  // 使用类名和标签
  let selector = '';
  let current = element;

  while (current && current !== document.body) {
    let part = current.tagName.toLowerCase();

    if (current.id) {
      part = `#${current.id}`;
      selector = part + ' > ' + selector;
      break;
    }

    if (current.className && typeof current.className === 'string') {
      const classes = current.className.trim().split(/\s+/).filter(c => c);
      if (classes.length > 0 && classes[0]) {
        part += '.' + classes.slice(0, 2).join('.');
      }
    }

    // 添加 nth-child 以提高准确性
    if (current.parentNode) {
      const siblings = Array.from(current.parentNode.children);
      const index = siblings.indexOf(current);
      if (siblings.filter(s => s.tagName === current.tagName).length > 1) {
        part += `:nth-child(${index + 1})`;
      }
    }

    selector = part + ' > ' + selector;
    current = current.parentNode;
  }

  // 清理选择器
  selector = selector.replace(' > ', '').replace(/ > $/g, '');

  return selector || element.tagName.toLowerCase();
}

// 获取元素的可读描述
function getElementDescription(element) {
  if (!element) return '';

  const tag = element.tagName.toLowerCase();
  const id = element.id;
  const className = element.className;
  const text = element.textContent?.trim().substring(0, 50) || '';
  const name = element.getAttribute('name') || '';
  const placeholder = element.getAttribute('placeholder') || '';

  let desc = tag;
  if (id) desc += `#${id}`;
  if (className && typeof className === 'string') {
    const firstClass = className.split(' ')[0];
    if (firstClass) desc += `.${firstClass}`;
  }
  if (text) desc += ` "${text}"`;
  if (name) desc += ` [name=${name}]`;
  if (placeholder) desc += ` [placeholder=${placeholder}]`;

  return desc;
}

// 点击事件处理
function handleClick(event) {
  if (!isRecording) return;
  if (event.button !== 0) return; // 只记录左键点击

  const target = event.target;
  if (shouldIgnore(target)) return;

  const selector = getSelector(target);
  const description = getElementDescription(target);

  chrome.runtime.sendMessage({
    type: 'RECORD_ACTION',
    action: {
      type: 'click',
      selector: selector,
      description: `点击: ${description}`,
      tagName: target.tagName.toLowerCase(),
      text: target.textContent?.trim().substring(0, 100),
      x: event.clientX,
      y: event.clientY,
      screenshot: true
    }
  }).catch(() => {});
}

// 双击事件处理
function handleDblClick(event) {
  if (!isRecording) return;

  const target = event.target;
  if (shouldIgnore(target)) return;

  const selector = getSelector(target);
  const description = getElementDescription(target);

  chrome.runtime.sendMessage({
    type: 'RECORD_ACTION',
    action: {
      type: 'dblclick',
      selector: selector,
      description: `双击：${description}`,
      tagName: target.tagName.toLowerCase()
    }
  }).catch(() => {});
}

// 输入事件处理
function handleInput(event) {
  if (!isRecording) return;

  const target = event.target;
  if (shouldIgnore(target)) return;
  if (target.tagName !== 'INPUT' && target.tagName !== 'TEXTAREA') return;

  const selector = getSelector(target);
  const value = target.value;
  const description = getElementDescription(target);

  chrome.runtime.sendMessage({
    type: 'RECORD_ACTION',
    action: {
      type: 'input',
      selector: selector,
      value: value,
      description: `输入：${description}`,
      tagName: target.tagName.toLowerCase(),
      inputType: target.type || 'text'
    }
  }).catch(() => {});
}

// 改变事件处理（select, checkbox, radio）
function handleChange(event) {
  if (!isRecording) return;

  const target = event.target;
  if (shouldIgnore(target)) return;

  const selector = getSelector(target);
  const description = getElementDescription(target);

  let action = null;

  if (target.tagName === 'SELECT') {
    const selectedOption = target.options[target.selectedIndex];
    action = {
      type: 'select',
      selector: selector,
      value: target.value,
      text: selectedOption?.textContent,
      description: `选择选项：${description}`
    };
  } else if (target.type === 'checkbox') {
    action = {
      type: target.checked ? 'check' : 'uncheck',
      selector: selector,
      checked: target.checked,
      description: `${target.checked ? '勾选' : '取消勾选'}: ${description}`
    };
  } else if (target.type === 'radio') {
    action = {
      type: 'check',
      selector: selector,
      value: target.value,
      checked: target.checked,
      description: `选择单选按钮：${description}`
    };
  }

  if (action) {
    chrome.runtime.sendMessage({
      type: 'RECORD_ACTION',
      action: action
    }).catch(() => {});
  }
}

// 键盘事件处理
function handleKeyDown(event) {
  if (!isRecording) return;

  const target = event.target;
  if (shouldIgnore(target)) return;

  // 特殊按键处理
  const specialKeys = ['Enter', 'Escape', 'Tab', 'Backspace', 'Delete', 'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'];

  if (specialKeys.includes(event.key)) {
    const selector = getSelector(target);
    const description = getElementDescription(target);

    chrome.runtime.sendMessage({
      type: 'RECORD_ACTION',
      action: {
        type: 'keypress',
        selector: selector,
        key: event.key,
        code: event.code,
        description: `按键：${event.key} - ${description}`
      }
    }).catch(() => {});
  }
}

// 焦点事件处理
function handleFocus(event) {
  if (!isRecording) return;

  const target = event.target;
  if (shouldIgnore(target)) return;
  if (target.tagName !== 'INPUT' && target.tagName !== 'TEXTAREA' && target.tagName !== 'SELECT') return;

  const selector = getSelector(target);

  chrome.runtime.sendMessage({
    type: 'RECORD_ACTION',
    action: {
      type: 'focus',
      selector: selector,
      description: `聚焦：${getElementDescription(target)}`
    }
  }).catch(() => {});
}

// 滚动事件处理（带节流）
let scrollTimeout = null;
function handleScroll(event) {
  if (!isRecording) return;
  if (scrollTimeout) return;

  scrollTimeout = setTimeout(() => {
    chrome.runtime.sendMessage({
      type: 'RECORD_ACTION',
      action: {
        type: 'scroll',
        x: window.scrollX,
        y: window.scrollY,
        description: `滚动到 (${window.scrollX}, ${window.scrollY})`
      }
    }).catch(() => {});
    scrollTimeout = null;
  }, 1000);
}

// 页面卸载前处理
function handleBeforeUnload(event) {
  if (!isRecording) return;

  chrome.runtime.sendMessage({
    type: 'RECORD_ACTION',
    action: {
      type: 'page_close',
      url: window.location.href,
      title: document.title,
      description: '页面关闭'
    }
  }).catch(() => {});
}

// 判断是否应该忽略的元素
function shouldIgnore(element) {
  if (!element || !element.tagName) return true;

  // 忽略特定标签
  if (IGNORED_TAGS.includes(element.tagName)) return true;

  // 忽略 contenteditable 元素内部的编辑事件
  if (element.isContentEditable) return true;

  // 忽略扩展 UI
  if (element.closest?.('#testflowai-recorder-indicator')) return true;

  return false;
}

// 显示录制指示器
function showRecordingIndicator() {
  // 移除已存在的指示器
  hideRecordingIndicator();

  const indicator = document.createElement('div');
  indicator.id = 'testflowai-recorder-indicator';
  indicator.innerHTML = `
    <div style="
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 999999;
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      background: linear-gradient(135deg, #f5576c, #ee5a6f);
      border-radius: 30px;
      box-shadow: 0 4px 20px rgba(245, 87, 108, 0.4);
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      font-size: 14px;
      color: white;
      animation: pulse 2s infinite;
    ">
      <div style="
        width: 12px;
        height: 12px;
        background: white;
        border-radius: 50%;
        animation: blink 1s infinite;
      "></div>
      <span>🔴 录制中...</span>
    </div>
    <style>
      @keyframes blink {
        0%, 100% { opacity: 1; }
        50% { opacity: 0.3; }
      }
    </style>
  `;

  document.body.appendChild(indicator);
}

// 隐藏录制指示器
function hideRecordingIndicator() {
  const indicator = document.getElementById('testflowai-recorder-indicator');
  if (indicator) {
    indicator.remove();
  }
}

// 页面导航监听
let lastUrl = location.href;
const observer = new MutationObserver(() => {
  if (lastUrl !== location.href && isRecording) {
    lastUrl = location.href;

    chrome.runtime.sendMessage({
      type: 'RECORD_ACTION',
      action: {
        type: 'navigate',
        url: location.href,
        title: document.title,
        description: `访问页面：${document.title || location.href}`
      }
    }).catch(() => {});
  }
});

// 启动 URL 变化监听
observer.observe(document.body, { childList: true, subtree: true });

// 初始化
init();

// 页面加载完成后自动检测是否需要开始录制
window.addEventListener('load', () => {
  chrome.runtime.sendMessage({ type: 'GET_RECORDING_STATUS' }, (response) => {
    if (response?.isRecording) {
      startRecording(response.tabId);
    }
  }).catch(() => {});
});

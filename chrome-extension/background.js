// TestFlowAI Recorder - Background Script
// 负责管理录制状态、存储操作序列、与 popup 通信

import { ActionRecorder } from './recorder.js';

// 全局录制器实例
let recorder = null;
let isRecording = false;
let currentTabId = null;

// 初始化
chrome.runtime.onInstalled.addListener(() => {
  console.log('TestFlowAI Recorder 已安装');
  // 创建右键菜单
  chrome.contextMenus.create({
    id: 'startRecording',
    title: '开始录制测试',
    contexts: ['all']
  });
  chrome.contextMenus.create({
    id: 'stopRecording',
    title: '停止录制',
    contexts: ['all']
  });
});

// 监听来自 content script 或 popup 的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  console.log('收到消息:', message);

  switch (message.type) {
    case 'START_RECORDING':
      startRecording(message.tabId);
      sendResponse({ success: true, isRecording: true });
      break;

    case 'STOP_RECORDING':
      stopRecording();
      sendResponse({ success: true, isRecording: false, actions: recorder?.getActions() });
      break;

    case 'GET_RECORDING_STATUS':
      sendResponse({ isRecording, actions: recorder?.getActions() || [] });
      break;

    case 'RECORD_ACTION':
      if (recorder && isRecording) {
        recorder.addAction(message.action);
        broadcastUpdate();
      }
      sendResponse({ success: true });
      break;

    case 'CLEAR_ACTIONS':
      if (recorder) {
        recorder.clearActions();
        broadcastUpdate();
      }
      sendResponse({ success: true });
      break;

    case 'GET_ACTIONS':
      sendResponse({ actions: recorder?.getActions() || [] });
      break;

    default:
      sendResponse({ success: false, error: '未知消息类型' });
  }

  return true; // 保持消息通道开放
});

// 开始录制
function startRecording(tabId) {
  isRecording = true;
  currentTabId = tabId;
  recorder = new ActionRecorder();

  console.log('开始录制，tabId:', tabId);

  // 向 content script 发送开始录制消息
  chrome.tabs.sendMessage(tabId, {
    type: 'START_RECORDING',
    tabId
  }).catch(err => {
    console.log('发送消息失败，尝试注入脚本:', err);
    injectContentScript(tabId);
  });

  // 更新扩展图标
  updateIcon(true);
}

// 停止录制
function stopRecording() {
  isRecording = false;

  if (currentTabId) {
    chrome.tabs.sendMessage(currentTabId, { type: 'STOP_RECORDING' }).catch(() => {});
  }

  updateIcon(false);

  const actions = recorder?.getActions() || [];
  console.log('停止录制，共录制', actions.length, '个操作');

  return actions;
}

// 更新扩展图标
function updateIcon(recording) {
  const iconPath = recording ? {
    '16': 'icons/icon-recording16.png',
    '48': 'icons/icon-recording48.png',
    '128': 'icons/icon-recording128.png'
  } : {
    '16': 'icons/icon16.png',
    '48': 'icons/icon48.png',
    '128': 'icons/icon128.png'
  };

  chrome.action.setIcon(iconPath);
}

// 广播更新到所有 popup
function broadcastUpdate() {
  chrome.runtime.sendMessage({
    type: 'ACTIONS_UPDATED',
    actions: recorder?.getActions() || []
  }).catch(() => {});
}

// 注入 content script
function injectContentScript(tabId) {
  chrome.scripting.executeScript({
    target: { tabId },
    files: ['content.js']
  }).then(() => {
    console.log('Content script 注入成功');
    chrome.tabs.sendMessage(tabId, { type: 'START_RECORDING', tabId });
  }).catch(err => {
    console.error('注入 content script 失败:', err);
  });
}

// 监听右键菜单点击
chrome.contextMenus.onClicked.addListener((info, tab) => {
  if (info.menuItemId === 'startRecording') {
    startRecording(tab.id);
  } else if (info.menuItemId === 'stopRecording') {
    stopRecording();
  }
});

// 监听标签页切换
chrome.tabs.onActivated.addListener((activeInfo) => {
  if (isRecording) {
    currentTabId = activeInfo.tabId;
    chrome.tabs.sendMessage(activeInfo.tabId, {
      type: 'START_RECORDING',
      tabId: activeInfo.tabId
    }).catch(() => {});
  }
});

// 监听标签页更新
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (isRecording && changeInfo.status === 'complete') {
    // 页面加载完成后重新注入
    chrome.tabs.sendMessage(tabId, {
      type: 'PAGE_LOADED',
      tabId
    }).catch(() => {
      injectContentScript(tabId);
    });
  }
});

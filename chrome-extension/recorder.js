// 动作录制器 - 存储和管理录制的动作序列

export class ActionRecorder {
  constructor() {
    this.actions = [];
    this.startTime = Date.now();
  }

  // 添加动作
  addAction(action) {
    action.timestamp = Date.now() - this.startTime;
    action.order = this.actions.length;
    this.actions.push(action);
    console.log('录制动作:', action.type, action);
  }

  // 获取所有动作
  getActions() {
    return [...this.actions];
  }

  // 清除所有动作
  clearActions() {
    this.actions = [];
    this.startTime = Date.now();
  }

  // 导出为 TestFlow 格式
  exportToTestFlow() {
    const steps = this.actions.map((action, index) => {
      return {
        id: index + 1,
        type: this.mapActionType(action.type),
        description: action.description || this.getActionDescription(action),
        selector: action.selector,
        value: action.value,
        expected: action.expected,
        screenshot: action.screenshot || false
      };
    });

    return {
      steps: JSON.stringify(steps, null, 2),
      variables: JSON.stringify({}, null, 2),
      tags: JSON.stringify(['recorded', new Date().toLocaleDateString()], null, 2)
    };
  }

  // 映射动作类型到 TestFlow 类型
  mapActionType(type) {
    const typeMap = {
      'click': 'click',
      'dblclick': 'doubleClick',
      'input': 'input',
      'select': 'select',
      'check': 'check',
      'uncheck': 'uncheck',
      'navigate': 'navigate',
      'scroll': 'scroll',
      'hover': 'hover',
      'assert': 'assert',
      'wait': 'wait'
    };
    return typeMap[type] || type;
  }

  // 生成动作描述
  getActionDescription(action) {
    switch (action.type) {
      case 'click':
        return `点击元素 ${action.selector || ''}`;
      case 'input':
        return `输入文本到 ${action.selector || ''}`;
      case 'navigate':
        return `访问页面 ${action.url || ''}`;
      case 'select':
        return `选择选项 ${action.value || ''}`;
      case 'assert':
        return `断言 ${action.expected || ''}`;
      default:
        return `${action.type} 操作`;
    }
  }
}

// 动作类型常量
export const ActionTypes = {
  CLICK: 'click',
  DBLCLICK: 'dblclick',
  INPUT: 'input',
  SELECT: 'select',
  CHECK: 'check',
  UNCHECK: 'uncheck',
  NAVIGATE: 'navigate',
  SCROLL: 'scroll',
  HOVER: 'hover',
  ASSERT: 'assert',
  WAIT: 'wait'
};

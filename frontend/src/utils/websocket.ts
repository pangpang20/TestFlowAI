import { Client, IMessage, IFrame } from '@stomp/stompjs'

/**
 * WebSocket 客户端工具类
 * 用于接收后端推送的实时执行日志
 */
class WebSocketClient {
  private client: Client | null = null
  private connected = false
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 1000

  /**
   * 连接到 WebSocket 服务器
   * @param executionId 执行 ID
   * @param onMessage 消息回调
   * @param onConnect 连接成功回调
   * @param onDisconnect 断开连接回调
   */
  public connect(
    executionId: string,
    onMessage: (message: any) => void,
    onConnect?: () => void,
    onDisconnect?: () => void
  ): void {
    if (this.connected) {
      console.log('[WebSocket] 已连接，跳过')
      return
    }

    const wsUrl = this.getWebSocketUrl()
    console.log('[WebSocket] 准备连接:', wsUrl)

    this.client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: this.reconnectDelay,
      connectionTimeout: 5000,
      onConnect: (frame: IFrame) => {
        console.log('[WebSocket] 连接成功', frame)
        this.connected = true
        this.reconnectAttempts = 0

        // 订阅执行日志主题
        const destination = `/topic/execution.${executionId}`
        console.log('[WebSocket] 订阅主题:', destination)

        this.client?.subscribe(destination, (message: IMessage) => {
          console.log('[WebSocket] 收到消息:', message.body)
          try {
            const parsed = JSON.parse(message.body)
            onMessage(parsed)
          } catch (e) {
            console.error('[WebSocket] 解析消息失败:', e)
          }
        })

        if (onConnect) {
          onConnect()
        }
      },
      onDisconnect: (frame: IFrame) => {
        console.log('[WebSocket] 断开连接', frame)
        this.connected = false
        if (onDisconnect) {
          onDisconnect()
        }
      },
      onStompError: (frame: IFrame) => {
        console.error('[WebSocket] STOMP 错误:', frame)
      },
      onWebSocketError: (event: Event) => {
        console.error('[WebSocket] WebSocket 错误:', event)
      }
    })

    this.client.activate()
  }

  /**
   * 断开 WebSocket 连接
   */
  public disconnect(): void {
    if (this.client) {
      this.client.deactivate()
      this.client = null
      this.connected = false
      console.log('[WebSocket] 已断开连接')
    }
  }

  /**
   * 获取 WebSocket URL
   */
  private getWebSocketUrl(): string {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || window.location.origin
    // 将 http/https 转换为 ws/wss
    const wsProtocol = baseUrl.startsWith('https') ? 'wss' : 'ws'
    const url = new URL(baseUrl)
    url.protocol = `${wsProtocol}:`
    if (!url.pathname.endsWith('/')) {
      url.pathname = url.pathname + '/'
    }
    url.pathname = url.pathname + 'ws'
    return url.toString()
  }

  /**
   * 检查是否已连接
   */
  public isConnected(): boolean {
    return this.connected && (this.client?.active ?? false)
  }
}

// 导出单例
export const wsClient = new WebSocketClient()

/**
 * WebSocket 消息类型
 */
export interface ExecutionMessage {
  type: 'step_start' | 'step_end' | 'log' | 'error' | 'complete' | 'screenshot'
  executionId: string
  stepId?: number
  description?: string
  status?: string
  errorMessage?: string
  content?: string
  totalSteps?: number
  passedSteps?: number
  failedSteps?: number
  screenshotPath?: string
  timestamp: number
}

export default WebSocketClient

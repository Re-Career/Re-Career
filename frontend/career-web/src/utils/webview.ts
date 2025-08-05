import { WebViewMessage } from '../types/webview'

export const sendMessageToNative = ({ type }: WebViewMessage) => {
  const message: WebViewMessage = { type }

  window.ReactNativeWebView?.postMessage(JSON.stringify(message))
}

export const WebViewMessageTypes = {
  LOGIN: 'LOGIN',
  CLOSE_WEBVIEW: 'CLOSE_WEBVIEW',
  NAVIGATE_BACK: 'NAVIGATE_BACK',
} as const

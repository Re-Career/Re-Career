import { WebViewMessage } from '../types/webview'

export const sendMessageToNative = ({ type, data }: WebViewMessage) => {
  const message: WebViewMessage = { type, data }

  window.ReactNativeWebView?.postMessage(JSON.stringify(message))
}

export const sendAuthTokensToNative = (
  accessToken: string,
  refreshToken: string
) => {
  sendMessageToNative({
    type: WebViewMessageTypes.SAVE_AUTH,
    data: {
      accessToken,
      refreshToken,
    },
  })
}

export const WebViewMessageTypes = {
  LOGIN: 'LOGIN',
  CLOSE_WEBVIEW: 'CLOSE_WEBVIEW',
  NAVIGATE_BACK: 'NAVIGATE_BACK',
  SAVE_AUTH: 'SAVE_AUTH',
} as const

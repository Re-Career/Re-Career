import { WEBVIEW_MESSAGE_TYPES } from '@/lib/constants/global'
import { WebViewMessage } from '@/types/global'

export const sendMessageToNative = ({ type, data }: WebViewMessage) => {
  const message: WebViewMessage = { type, data }

  window.ReactNativeWebView?.postMessage(JSON.stringify(message))
}

export const sendAuthTokensToNative = (
  accessToken: string,
  refreshToken: string
) => {
  sendMessageToNative({
    type: WEBVIEW_MESSAGE_TYPES.SAVE_AUTH,
    data: {
      accessToken,
      refreshToken,
    },
  })
}

export const isWebView = (): boolean => {
  return typeof window !== 'undefined' && !!(window as any).ReactNativeWebView
}

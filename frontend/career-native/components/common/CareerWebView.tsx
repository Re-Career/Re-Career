import { useRouter } from 'expo-router'
import React, { useCallback } from 'react'
import WebView, {
  WebViewMessageEvent,
  WebViewProps,
} from 'react-native-webview'

const DEFAULT_WEB_APP_IP = 'http://0.0.0.0:3000'
const DEFAULT_WEB_APP_URL = 'http://localhost:3000'

interface CareerWebViewProps extends WebViewProps {
  path?: string
}

const CareerWebView = (props: CareerWebViewProps) => {
  const router = useRouter()

  const {
    path = '',
    style = { flex: 1 },
    javaScriptEnabled = true,
    domStorageEnabled = true,
    startInLoadingState = true,
    scalesPageToFit = true,
    ...restProps
  } = props

  // 개발 환경에서 실제 IP 주소 사용
  const getBaseUrl = useCallback(() => {
    if (__DEV__) {
      return process.env.EXPO_PUBLIC_WEB_APP_IP ?? DEFAULT_WEB_APP_IP
    }

    return process.env.EXPO_PUBLIC_WEB_APP_URL ?? DEFAULT_WEB_APP_URL
  }, [])

  const onMessage = (event: WebViewMessageEvent) => {
    const { type } = JSON.parse(event.nativeEvent.data)

    switch (type) {
      case 'LOGIN':
        return router.push('/(modal)/login')
      case 'CLOSE_WEBVIEW':
      case 'NAVAGATE_BACK':
        return router.back()
      default:
        return
    }
  }

  return (
    <WebView
      {...restProps}
      source={{ uri: `${getBaseUrl()}/${path}` }}
      style={style}
      javaScriptEnabled={javaScriptEnabled}
      domStorageEnabled={domStorageEnabled}
      startInLoadingState={startInLoadingState}
      scalesPageToFit={scalesPageToFit}
      onMessage={onMessage}
    />
  )
}

export default CareerWebView

import React from 'react'
import WebView, { WebViewProps } from 'react-native-webview'

const DEFAULT_WEB_APP_IP = 'http://0.0.0.0:3000'
const DEFAULT_WEB_APP_URL = 'http://localhost:3000'

interface CareerWebViewProps extends WebViewProps {
  path?: string
}

const CareerWebView = (props: CareerWebViewProps) => {
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
  const getBaseUrl = () => {
    if (__DEV__) {
      return process.env.EXPO_PUBLIC_WEB_APP_IP ?? DEFAULT_WEB_APP_IP
    }

    return process.env.EXPO_PUBLIC_WEB_APP_URL ?? DEFAULT_WEB_APP_URL
  }

  const uri = `${getBaseUrl()}/${path}`

  return (
    <WebView
      {...restProps}
      source={{ uri }}
      style={style}
      javaScriptEnabled={javaScriptEnabled}
      domStorageEnabled={domStorageEnabled}
      startInLoadingState={startInLoadingState}
      scalesPageToFit={scalesPageToFit}
    />
  )
}

export default CareerWebView

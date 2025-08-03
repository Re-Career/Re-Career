import React from 'react'
import WebView, { WebViewProps } from 'react-native-webview'

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

  const uri = `${process.env.EXPO_PUBLIC_WEB_APP_URL ?? 'http://localhost:3000'}/${path}`

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

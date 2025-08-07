import { DEFAULT_WEB_APP_IP, DEFAULT_WEB_APP_URL } from '@/constants/constants'
import { useRouter } from 'expo-router'
import React, { useCallback, useEffect, useState } from 'react'
import WebView, {
  WebViewMessageEvent,
  WebViewProps,
} from 'react-native-webview'
import { useAuth } from '@/hooks/useAuth'

interface CareerWebViewProps extends WebViewProps {
  path?: string
}

const CareerWebView = (props: CareerWebViewProps) => {
  const router = useRouter()
  const { loadTokens, saveTokens, clearTokens } = useAuth()

  const {
    path = '',
    style = { flex: 1 },
    javaScriptEnabled = true,
    domStorageEnabled = true,
    startInLoadingState = true,
    scalesPageToFit = true,
    ...restProps
  } = props

  const [isTokensReady, setIsTokensReady] = useState(false)
  const [injectedJS, setInjectedJS] = useState('')

  useEffect(() => {
    const setupTokens = async () => {
      const script = await injectTokensFromStorage()

      setInjectedJS(script)
      setIsTokensReady(true)
    }

    setupTokens()
  }, [])

  // 앱 시작시 토큰을 쿠키로 설정
  const injectTokensFromStorage = useCallback(async () => {
    try {
      const storedTokens = await loadTokens()

      if (storedTokens) {
        const { accessToken, refreshToken } = storedTokens

        // JavaScript로 쿠키 설정
        const cookieScript = `
          document.cookie = "accessToken=${accessToken}; path=/; max-age=86400";
          document.cookie = "refreshToken=${refreshToken}; path=/; max-age=604800"; 
          true;
        `

        return cookieScript
      }
    } catch (error) {
      console.error('토큰 주입 오류:', error)
    }

    return 'true;' // 토큰이 없어도 WebView는 로드
  }, [])

  // 개발 환경에서 실제 IP 주소 사용
  const getBaseUrl = useCallback(() => {
    if (__DEV__) {
      return process.env.EXPO_PUBLIC_WEB_APP_IP ?? DEFAULT_WEB_APP_IP
    }

    return process.env.EXPO_PUBLIC_WEB_APP_URL ?? DEFAULT_WEB_APP_URL
  }, [])

  const onMessage = useCallback(async (event: WebViewMessageEvent) => {
    const { type, data } = JSON.parse(event.nativeEvent.data)

    switch (type) {
      case 'LOGIN':
        return router.push('/(modal)/login')

      case 'SAVE_AUTH':
        const { accessToken, refreshToken } = data

        //TODO: 저장시 에러 처리 추가
        if (accessToken && refreshToken) {
          saveTokens(accessToken, refreshToken)
        }

        return router.back()

      case 'CLOSE_WEBVIEW':
      case 'NAVAGATE_BACK':
        return router.back()

      case 'CLEAR_TOKEN':
        clearTokens()

        return

      default:
        return
    }
  }, [])

  if (!isTokensReady) {
    return <></>
  }

  return (
    <WebView
      {...restProps}
      source={{ uri: `${getBaseUrl()}/${path}` }}
      style={style}
      sharedCookiesEnabled={true}
      injectedJavaScript={injectedJS}
      javaScriptEnabled={javaScriptEnabled}
      domStorageEnabled={domStorageEnabled}
      startInLoadingState={startInLoadingState}
      scalesPageToFit={scalesPageToFit}
      onMessage={onMessage}
    />
  )
}

export default CareerWebView

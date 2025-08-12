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

interface WebviewData {
  mentorId?: number
  jobId?: number
  accessToken?: string
  refreshToken?: string
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
      try {
        const script = await injectTokensFromStorage()

        setInjectedJS(script)
        setIsTokensReady(true)
      } catch (error) {
        console.error('토큰 설정 실패:', error)
        setIsTokensReady(true) // 토큰 없어도 웹뷰 로드
      }
    }

    setupTokens()

    return () => {}
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
    try {
      const { type, data } = JSON.parse(event.nativeEvent.data)

      const {
        mentorId = 0,
        jobId = 0,
        accessToken = '',
        refreshToken = '',
      } = (data || {}) as WebviewData

      switch (type) {
        case 'LOGIN_MODAL':
          return router.push('/(modal)/login')

        case 'SAVE_AUTH':
          //TODO: 저장시 에러 처리 추가
          if (accessToken && refreshToken) {
            saveTokens(accessToken, refreshToken)
          }

          return router.back()

        case 'CLEAR_TOKEN':
          return clearTokens()

        case 'CLOSE_WEBVIEW':
        case 'NAVIGATE_BACK':
          return router.back()

        case 'SEARCH_MENTOR':
          return router.push(`/matching?jobId=${jobId}`)

        case 'MENTOR_PROFILE':
          return router.push(`/mentor/${mentorId}/profile`)

        case 'MENTOR_RESERVATION':
          return router.push(`/mentor/${mentorId}/reservation`)

        default:
          return
      }
    } catch (error) {
      console.error(error)
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

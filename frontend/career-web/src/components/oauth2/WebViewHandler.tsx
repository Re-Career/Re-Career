'use client'

import { useEffect } from 'react'
import { sendAuthTokensToNative, isWebView } from '@/utils/webview'
import { useRouter } from 'next/navigation'

interface WebViewHandlerProps {
  accessToken: string
  refreshToken: string
  redirectUrl: string
}

const WebViewHandler = ({
  accessToken,
  refreshToken,
  redirectUrl,
}: WebViewHandlerProps) => {
  const router = useRouter()

  useEffect(() => {
    if (isWebView()) {
      sendAuthTokensToNative(accessToken, refreshToken)
    }

    router.replace(redirectUrl)
  }, [accessToken, refreshToken])

  return null
}

export default WebViewHandler

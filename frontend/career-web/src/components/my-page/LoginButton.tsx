'use client'

import { WebViewMessageTypes } from '@/lib/constants/global'
import { sendMessageToNative } from '@/utils/webview'
import { useRouter } from 'next/navigation'

import React from 'react'

const LoginButton = () => {
  const router = useRouter()

  const handleLogin = () => {
    if (window.ReactNativeWebView) {
      sendMessageToNative({ type: WebViewMessageTypes.LOGIN_MODAL })
    } else {
      router.push('/login')
    }
  }

  return (
    <button onClick={handleLogin} className="rounded-lg border px-4 py-2">
      로그인 테스트
    </button>
  )
}

export default LoginButton

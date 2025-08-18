'use client'

import { clearToken } from '@/app/actions/auth/action'
import { WEBVIEW_MESSAGE_TYPES } from '@/lib/constants/global'
import { sendMessageToNative } from '@/utils/webview'
import React from 'react'

const LogoutButton = () => {
  const handleLogout = async () => {
    if (window.ReactNativeWebView) {
      sendMessageToNative({ type: WEBVIEW_MESSAGE_TYPES.CLEAR_TOKEN })
    }

    await clearToken()
  }

  return (
    <button onClick={handleLogout} className="rounded-lg border px-4 py-2">
      Logout
    </button>
  )
}

export default LogoutButton

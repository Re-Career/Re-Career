'use client'

import { useEffect } from 'react'
import { WebViewMessageTypes } from '@/lib/constants/global'
import { sendAuthTokensToNative, sendMessageToNative } from '@/utils/webview'
import { getToken } from '@/app/actions/auth/action'

export default function SignUpSuccessPage() {
  useEffect(() => {
    const handleSuccess = async () => {
      const accessToken = await getToken()

      sendAuthTokensToNative(accessToken, '')
      sendMessageToNative({ type: WebViewMessageTypes.CLOSE_WEBVIEW })
    }

    handleSuccess()
  }, [])

  return <></>
}

'use client'

import { useEffect } from 'react'
import { sendAuthTokensToNative } from '@/utils/webview'
import { getToken } from '@/app/actions/auth/action'

export default function SignUpSuccessPage() {
  useEffect(() => {
    const handleSuccess = async () => {
      const accessToken = await getToken()

      sendAuthTokensToNative(accessToken, '')
    }

    handleSuccess()
  }, [])

  return <></>
}

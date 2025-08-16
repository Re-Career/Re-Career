'use client'

import { sendAuthTokensToNative } from '@/utils/webview'
import { useSearchParams } from 'next/navigation'
import { useEffect } from 'react'

const AuthRedirectPage = () => {
  const searchParams = useSearchParams()
  const accessToken = searchParams.get('accessToken')
  const refreshToken = searchParams.get('refreshToken')

  useEffect(() => {
    sendAuthTokensToNative(accessToken as string, refreshToken as string)
  }, [accessToken, refreshToken])

  return <></>
}

export default AuthRedirectPage

'use client'

import { isWebView, sendAuthTokensToNative } from '@/lib/utils/webview'
import { useSearchParams } from 'next/navigation'
import { useRouter } from 'next/navigation'
import { useEffect, Suspense } from 'react'

const AuthRedirectContent = () => {
  const router = useRouter()
  const params = useSearchParams()

  const accessToken = params.get('accessToken') ?? ''
  const refreshToken = params.get('refreshToken') ?? ''

  useEffect(() => {
    if (isWebView()) {
      sendAuthTokensToNative(accessToken, refreshToken)
    }
  }, [accessToken, refreshToken, router])

  return <></>
}

const AuthRedirectPage = () => {
  return (
    <Suspense fallback={<></>}>
      <AuthRedirectContent />
    </Suspense>
  )
}

export default AuthRedirectPage

'use client'

import { isWebView, sendAuthTokensToNative } from '@/utils/webview'
import { useSearchParams } from 'next/navigation'
import { useRouter } from 'next/navigation'
import { useEffect, Suspense } from 'react'

const AuthRedirectContent = () => {
  const router = useRouter()
  const params = useSearchParams()

  const accessToken = params.get('accessToken') ?? ''
  const refreshToken = params.get('refreshToken') ?? ''
  const redirectUrl = params.get('redirectUrl') ?? '/'

  useEffect(() => {
    if (isWebView()) {
      sendAuthTokensToNative(accessToken, refreshToken)
    }

    router.replace(redirectUrl)
  }, [accessToken, refreshToken, redirectUrl, router])

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

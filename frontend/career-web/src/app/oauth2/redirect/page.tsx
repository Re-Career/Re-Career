'use client'

import { sendAuthTokensToNative } from '@/utils/webview'
import { useSearchParams } from 'next/navigation'
import { useEffect, Suspense } from 'react'

const AuthRedirectContent = () => {
  const searchParams = useSearchParams()
  const accessToken = searchParams.get('accessToken')
  const refreshToken = searchParams.get('refreshToken')

  useEffect(() => {
    sendAuthTokensToNative(accessToken as string, refreshToken as string)
  }, [accessToken, refreshToken])

  return <></>
}

const AuthRedirectPage = () => {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <AuthRedirectContent />
    </Suspense>
  )
}

export default AuthRedirectPage

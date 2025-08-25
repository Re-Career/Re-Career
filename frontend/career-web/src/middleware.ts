import { NextRequest, NextResponse } from 'next/server'
import { handleOAuth2Redirect } from '@/lib/middleware/oauth'
import { COOKIE_OPTIONS, ROLE_TYPES } from './lib/constants/global'
import { RoleType } from '@/types/global'

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl

  if (pathname.startsWith('/sign-up/')) {
    const accessToken = request.cookies.get('accessToken')
    const refreshToken = request.cookies.get('refreshToken')
    const hasTokens = accessToken || refreshToken

    if (hasTokens) {
      return NextResponse.redirect(new URL('/', request.url))
    }

    const pendingAccessToken = request.cookies.get('pendingAccessToken')
    const pendingRefreshToken = request.cookies.get('pendingRefreshToken')
    const role = pathname.split('/sign-up/')[1]
    const _role = role?.toLocaleUpperCase() as RoleType
    const hasPendingTokens = pendingAccessToken || pendingRefreshToken

    if (!hasPendingTokens || !Object.values(ROLE_TYPES).includes(_role)) {
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  switch (pathname) {
    case '/oauth2/redirect':
      return await handleOAuth2Redirect(request)

    case '/my-page':
      const accessToken = request.cookies.get('accessToken')

      if (!accessToken) {
        const response = NextResponse.redirect(new URL('/login', request.url))

        response.cookies.set('redirectUrl', pathname, {
          ...COOKIE_OPTIONS,
          maxAge: 30 * 60,
        })

        return response
      }
      break
  }

  return NextResponse.next()
}

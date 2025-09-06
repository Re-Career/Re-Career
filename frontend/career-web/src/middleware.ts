import { NextRequest, NextResponse } from 'next/server'
import { handleOAuth2Redirect } from '@/lib/middleware/oauth'
import { refreshTokenInMiddleware } from '@/lib/middleware/token-refresh'
import { shouldRefreshToken } from '@/lib/utils/jwt-validator'
import { COOKIE_OPTIONS, ROLE_TYPES } from './lib/constants/global'
import { RoleType } from '@/types/global'

// 보호된 경로 정의
const protectedPaths = [
  '/my-page',
  '/mentor/reservation',
  '/settings',
  '/session',
  '/sessions',
]

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl

  const accessToken = request.cookies.get('accessToken')?.value
  const refreshToken = request.cookies.get('refreshToken')?.value
  const pendingAccessToken = request.cookies.get('pendingAccessToken')?.value
  const pendingRefreshToken = request.cookies.get('pendingRefreshToken')?.value

  const hasTokens = accessToken || refreshToken
  const hasPendingTokens = pendingAccessToken || pendingRefreshToken

  // 보호된 경로인지 확인
  const isProtectedPath =
    pathname !== '/' && protectedPaths.some((path) => pathname.startsWith(path))

  // 보호된 경로일 때만 토큰 갱신 확인
  if (isProtectedPath && shouldRefreshToken(request)) {
    const refreshedResponse = await refreshTokenInMiddleware(request)

    if (refreshedResponse) {
      // 토큰 갱신 성공 - 요청 계속 진행
      return refreshedResponse
    } else {
      // 토큰 갱신 실패 - 로그인 페이지로
      const response = NextResponse.redirect(new URL('/login', request.url))

      response.cookies.set('redirectUrl', pathname, {
        ...COOKIE_OPTIONS,
        maxAge: 30 * 60,
      })

      return response
    }
  }

  // 경로별 처리
  if (pathname.startsWith('/sign-up/')) {
    if (hasTokens) {
      return NextResponse.redirect(new URL('/', request.url))
    }

    const role = pathname.split('/sign-up/')[1]
    const _role = role?.toLocaleUpperCase() as RoleType

    if (!hasPendingTokens || !Object.values(ROLE_TYPES).includes(_role)) {
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  switch (pathname) {
    case '/login':
      if (hasTokens) {
        return NextResponse.redirect(new URL('/', request.url))
      }

      break

    case '/oauth2/redirect':
      return await handleOAuth2Redirect(request)

    case '/my-page':
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

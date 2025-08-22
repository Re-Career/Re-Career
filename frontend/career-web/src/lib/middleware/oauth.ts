import { NextRequest, NextResponse } from 'next/server'
import { COOKIE_OPTIONS, ONE_DAY } from '../constants/global'
import { getAuthMe } from '@/services/auth'

export async function handleOAuth2Redirect(
  request: NextRequest
): Promise<NextResponse> {
  const { searchParams } = request.nextUrl
  const accessToken = searchParams.get('accessToken')
  const refreshToken = searchParams.get('refreshToken')

  if (!accessToken || !refreshToken) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  try {
    const { errorMessage, data: auth } = await getAuthMe(accessToken)

    if (errorMessage) {
      throw new Error(errorMessage)
    }

    if (!auth) {
      throw new Error('접근 권한이 없습니다.')
    }

    // 가입을 안한 유저
    if (!auth.signupCompleted) {
      const response = NextResponse.redirect(
        new URL(`/sign-up/MENTEE`, request.url)
      )

      response.cookies.set('pendingAccessToken', accessToken, {
        maxAge: 30 * 60,
        ...COOKIE_OPTIONS,
      })

      response.cookies.set('pendingRefreshToken', refreshToken, {
        maxAge: 30 * 60,
        ...COOKIE_OPTIONS,
      })

      return response
    }

    // 정상 로그인된 유저
    const redirectUrl = request.cookies.get('redirectUrl')?.value || '/my-page'
    const response = NextResponse.redirect(new URL(redirectUrl, request.url))

    response.cookies.set('accessToken', accessToken, {
      maxAge: ONE_DAY, // 1일
      ...COOKIE_OPTIONS,
    })

    response.cookies.set('refreshToken', refreshToken, {
      maxAge: 7 * ONE_DAY, // 7일
      ...COOKIE_OPTIONS,
    })

    // redirectUrl 쿠키 삭제
    request.cookies.get('redirectUrl')?.value &&
      response.cookies.delete('redirectUrl')

    return response
  } catch (error) {
    console.log(error)

    return NextResponse.redirect(new URL('/login', request.url))
  }
}

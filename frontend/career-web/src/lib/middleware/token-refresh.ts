import { NextRequest, NextResponse } from 'next/server'
import { COOKIE_OPTIONS, ONE_DAY } from '../constants/global'

export const refreshTokenInMiddleware = async (
  request: NextRequest
): Promise<NextResponse | null> => {
  const refreshToken = request.cookies.get('refreshToken')?.value

  if (!refreshToken) {
    return null // RefreshToken이 없으면 갱신 불가
  }

  try {
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/auth/refresh`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${refreshToken}`,
          'Content-Type': 'application/json',
        },
      }
    )

    if (!response.ok) {
      return null // 갱신 실패
    }

    const data = await response.json()
    const newAccessToken = data.data

    if (!newAccessToken) {
      return null // 새 토큰이 없으면 실패
    }

    // 현재 요청을 계속 진행하되, 새 AccessToken 쿠키 설정
    const nextResponse = NextResponse.next()

    nextResponse.cookies.set('accessToken', newAccessToken, {
      maxAge: ONE_DAY,
      ...COOKIE_OPTIONS,
    })

    return nextResponse
  } catch (error) {
    console.error('Token refresh failed in middleware:', error)

    return null
  }
}

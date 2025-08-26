import { NextRequest } from 'next/server'

interface JWTPayload {
  sub: string
  role: string
  iat: number
  exp: number
}

export const isTokenValid = (token: string): boolean => {
  try {
    const parts = token.split('.')

    if (parts.length !== 3) return false

    const payload = JSON.parse(atob(parts[1])) as JWTPayload
    const currentTime = Math.floor(Date.now() / 1000)
    const isExpired = payload.exp > currentTime

    // 토큰이 만료되지 않았고, 필수 필드가 있는지 확인
    return !isExpired && !!payload.sub && !!payload.role
  } catch {
    return false
  }
}

export const shouldRefreshToken = (request: NextRequest): boolean => {
  const accessToken = request.cookies.get('accessToken')?.value
  const refreshToken = request.cookies.get('refreshToken')?.value

  // AccessToken이 없거나 유효하지 않으면서, RefreshToken이 있고 유효한 경우
  return (
    (!accessToken || !isTokenValid(accessToken)) &&
    !!refreshToken &&
    isTokenValid(refreshToken)
  )
}

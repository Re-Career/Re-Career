export interface JWTPayload {
  role: string
  userId: number
  exp: number
  iat: number
}

export function decodeJWT(token: string): JWTPayload | null {
  try {
    // JWT의 payload 부분만 추출 (두 번째 부분)
    const base64Url = token.split('.')[1]

    if (!base64Url) return null

    // Base64URL을 Base64로 변환
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')

    // Base64 디코딩
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
        .join('')
    )

    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('JWT decode error:', error)

    return null
  }
}

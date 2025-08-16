import { NextRequest, NextResponse } from 'next/server'

export async function middleware(request: NextRequest) {
  const { pathname, searchParams } = request.nextUrl

  if (pathname === '/my-page') {
    const accessToken = request.cookies.get('accessToken')

    if (!accessToken) {
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  if (pathname === '/oauth2/redirect') {
    const accessToken = searchParams.get('accessToken')
    const refreshToken = searchParams.get('refreshToken')

    if (!accessToken || !refreshToken) {
      return NextResponse.redirect(new URL('/login', request.url))
    }

    try {
      const authResponse = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/auth/me`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      )

      const auth = await authResponse.json()

      if (!auth.data.signupCompleted) {
        const response = NextResponse.redirect(
          new URL('/sign-up/mentee', request.url)
        )

        response.cookies.set('pendingAccessToken', accessToken, {
          httpOnly: true,
          secure: process.env.NODE_ENV === 'production',
          sameSite: 'strict',
          maxAge: 30 * 60,
          path: '/',
        })
        response.cookies.set('pendingRefreshToken', refreshToken, {
          httpOnly: true,
          secure: process.env.NODE_ENV === 'production',
          sameSite: 'strict',
          maxAge: 30 * 60,
          path: '/',
        })

        return response
      }

      const response = NextResponse.next()

      response.cookies.set('accessToken', accessToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        maxAge: 24 * 60 * 60,
        path: '/',
      })

      response.cookies.set('refreshToken', refreshToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        maxAge: 7 * 24 * 60 * 60,
        path: '/',
      })

      return response
    } catch {
      return NextResponse.redirect(new URL('/login', request.url))
    }
  }

  return NextResponse.next()
}

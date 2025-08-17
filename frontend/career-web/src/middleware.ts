import { NextRequest, NextResponse } from 'next/server'

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl

  if (pathname === '/my-page') {
    const accessToken = request.cookies.get('accessToken')

    if (!accessToken) {
      const response = NextResponse.redirect(new URL('/login', request.url))

      response.cookies.set('redirectUrl', pathname, {
        httpOnly: false,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
        maxAge: 30 * 60,
        path: '/',
      })

      return response
    }
  }

  return NextResponse.next()
}

import { NextRequest, NextResponse } from 'next/server'
import { handleOAuth2Redirect } from '@/lib/middleware/oauth'
import { COOKIE_OPTIONS } from './lib/constants/global'

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl

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
  }

  return NextResponse.next()
}

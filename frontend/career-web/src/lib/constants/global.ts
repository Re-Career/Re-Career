import { ResponseCookie } from 'next/dist/compiled/@edge-runtime/cookies'

export const WEBVIEW_MESSAGE_TYPES = {
  LOGIN_MODAL: 'LOGIN_MODAL',
  CLOSE_WEBVIEW: 'CLOSE_WEBVIEW',
  NAVIGATE_BACK: 'NAVIGATE_BACK',
  SAVE_AUTH: 'SAVE_AUTH',
  CLEAR_TOKEN: 'CLEAR_TOKEN',
  SEARCH_MENTOR: 'SEARCH_MENTOR',
  MENTOR_PROFILE: 'MENTOR_PROFILE',
} as const

export const ROLE_TYPES = {
  MENTEE: 'MENTEE',
  MENTOR: 'MENTOR',
} as const

export const ONE_DAY = 24 * 60 * 60

export const COOKIE_OPTIONS: Partial<ResponseCookie> = {
  httpOnly: false,
  secure: process.env.NODE_ENV === 'production',
  sameSite: 'strict',
  path: '/',
}

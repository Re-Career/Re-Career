export const WebViewMessageTypes = {
  LOGIN_MODAL: 'LOGIN_MODAL',
  CLOSE_WEBVIEW: 'CLOSE_WEBVIEW',
  NAVIGATE_BACK: 'NAVIGATE_BACK',
  SAVE_AUTH: 'SAVE_AUTH',
  CLEAR_TOKEN: 'CLEAR_TOKEN',
  SEARCH_MENTOR: 'SEARCH_MENTOR',
} as const

export const RoleTypes = {
  Mentee: 'mentee',
  Mentor: 'mentor',
} as const

export const OneDay = 24 * 60 * 60

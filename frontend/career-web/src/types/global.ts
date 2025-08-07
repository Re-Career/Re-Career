import { RoleTypes } from '@/lib/constants/global'

declare global {
  interface Window {
    ReactNativeWebView?: {
      postMessage: (message: string) => void
    }
  }
}

export interface SaveAuthData {
  accessToken: string
  refreshToken: string
}

export interface WebViewMessage {
  type: string
  data?: SaveAuthData | string
}

export type RoleType = (typeof RoleTypes)[keyof typeof RoleTypes]

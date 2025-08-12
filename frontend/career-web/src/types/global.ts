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

export interface SearchMentorData {
  jobId: string
}

export interface MentorProfileData {
  mentorId: number
}

export interface WebViewMessage {
  type: string
  data?: SaveAuthData | SearchMentorData | MentorProfileData | string
}

export type RoleType = (typeof RoleTypes)[keyof typeof RoleTypes]

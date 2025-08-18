import { ROLE_TYPES } from '@/lib/constants/global'

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

export type RoleType = (typeof ROLE_TYPES)[keyof typeof ROLE_TYPES]

export type DatePiece = Date | null

export type DateType = DatePiece | [DatePiece, DatePiece]

export interface FetchResponse<T> {
  isSuccess: boolean
  errorMessage: string
  data: T
}

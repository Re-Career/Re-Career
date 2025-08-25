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
  positionId: string
}

export interface MentorProfileData {
  mentorId: number
}

export interface WebViewMessage {
  type: string
  data?: SaveAuthData | SearchMentorData | MentorProfileData | string
}

export type RoleType = (typeof ROLE_TYPES)[keyof typeof ROLE_TYPES]

export type DatePiece = string | Date | null

export type DateType = DatePiece | [DatePiece, DatePiece]

export interface FetchResponse<T> {
  errorMessage: string
  data: T
  errors?: Record<string, string>
  status?: number
}

export interface ResponseMessage {
  message: string
  data: string
}

export type Nullable<T> = null | T

export interface DefaultData {
  id: number
  name: string
}

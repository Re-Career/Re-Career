import { DefaultData } from './global'

export interface PostSessionPayload {
  mentorId: number
  sessionTime: string
}

export interface PostSessionResponse {
  id: number
  mentorName: string
  menteeName: string
  sessionTime: string
}

export enum SessionStatus {
  REQUESTED = 'REQUESTED', // 멘토링 요청
  CONFIRMED = 'CONFIRMED', // 멘토링 확인
  CANCELED = 'CANCELED', // 멘토링 취소 (노쇼 포함)
  COMPLETED = 'COMPLETED', // 멘토링 완료
}

export interface SessionResponse {
  sessionId: number
  sessionTime: string
  status: SessionStatus
  mentor: {
    mentorId: number
    name: string
    position: DefaultData
    profileImageUrl: string
  }
  mentee: {
    menteeId: number
    name: string
    profileImageUrl: string
  } | null
}

export interface Session {
  id: number
  sessionTime: string
  status: SessionStatus
  mentor: {
    mentorId: number
    name: string
    position: DefaultData
    profileImageUrl: string
  }
  mentee: {
    menteeId: number
    name: string
    profileImageUrl: string
  }
}

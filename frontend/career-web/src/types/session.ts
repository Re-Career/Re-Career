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

export interface Session {
  id: number
  sessionTime: string
  status: string
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

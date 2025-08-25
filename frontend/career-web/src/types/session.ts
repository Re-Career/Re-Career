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

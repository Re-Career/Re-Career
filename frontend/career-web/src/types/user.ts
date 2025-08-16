export interface User {
  id: number
  name: string
  email: null | string
  role: string
  profileImageUrl: null | string
  region: null | string
  mentorId: null | number
  mentorPosition: null | string
  mentorDescription: null | string
  mentorIsVerified: null | boolean
  personalityTags: []
  signupCompleted: boolean
}

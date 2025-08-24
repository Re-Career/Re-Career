import { Nullable, RoleType } from './global'
import { PersonalityTag } from './personality-tags'

export interface User {
  id: number
  name: string
  email: string
  role: RoleType
  profileImageUrl: Nullable<string>
  provinceId: number
  provinceName: string
  cityId: Nullable<number>
  cityName: Nullable<string>
  mentorId: Nullable<number>
  mentorPosition: Nullable<string>
  mentorDescription: Nullable<string>
  mentorIsVerified: Nullable<boolean>
  personalityTags: PersonalityTag[]
  signupCompleted: boolean
}

export interface PutUserPayload {
  name: string
  email: string
  mentorPosition?: string
  mentorDescription?: string
}

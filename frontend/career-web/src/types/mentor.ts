import { DefaultData } from './global'
import { PersonalityTag } from './personality-tags'

export interface Mentor {
  id: number
  name: string
  position: DefaultData
  email: string
  profileImageUrl: string
  company: DefaultData
  experience: 0
  province: DefaultData
  meetingType: string
  personalityTags: PersonalityTag[]
}

export interface MentorDetail extends Mentor {
  shortDescription?: string
  introduction?: string
  skills?: DefaultData[]
  career?: string[]
  feedback?: {
    rating: number
    count: number
    comments: {
      id: number
      user: string
      rating: number
      comment: string
      date: string
    }[]
  }
}

export interface FilterConfig {
  key: string
  title: string
  options: DefaultData[]
}

export type FilterOptions = Record<string, string[]>

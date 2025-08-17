export interface PersonalityTag {
  id: number
  name: string
}

export interface Mentor {
  id: number
  name: string
  job: string
  email: string
  profileImageUrl: string
  company?: string
  experience?: number
  location?: string
  meetingType?: 'online' | 'offline' | 'both'
  personalityTags?: PersonalityTag[]
  shortDescription?: string
  introduction?: string
  skills?: string[]
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

export type FilterOptions = Record<string, string[]>

export interface FilterConfig {
  key: string
  title: string
  options: FilterOptions[]
}

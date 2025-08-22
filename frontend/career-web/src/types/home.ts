export interface TrendPosition {
  id: number
  name: string
  category: string
  imageUrl: string
}

export interface RegionPosition extends TrendPosition {
  rank: number
}

export interface RegionPositions {
  region: string
  positions: RegionPosition[]
}

export interface PersonalityTag {
  id: number
  name: string
}

export interface Mentor {
  id: number
  name: string
  position: string
  email: string
  profileImageUrl: string
  company?: string
  experience?: number
  region?: string
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

export interface IndustryNews {
  id: number
  category: string
  title: string
  summary: string
  imageUrl: string
}

export interface positionResponsibility {
  icon: string
  title: string
  description: string
}

export interface PositionDetail {
  id: number
  name: string
  category: string
  description: string
  famousPerson: {
    name: string
    image: string
    title: string
  }
  responsibilities: positionResponsibility[]
  industryTrends: string[]
}

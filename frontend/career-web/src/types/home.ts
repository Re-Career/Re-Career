export interface TrendJob {
  id: number
  name: string
  category: string
  imageUrl: string
}

export interface RegionJob extends TrendJob {
  rank: number
}

export interface RegionJobs {
  region: string
  positions: RegionJob[]
}

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

export interface JobResponsibility {
  icon: string
  title: string
  description: string
}

export interface JobDetail {
  id: number
  name: string
  category: string
  description: string
  famousPerson: {
    name: string
    image: string
    title: string
  }
  responsibilities: JobResponsibility[]
  industryTrends: string[]
}

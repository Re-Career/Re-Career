export interface TrendJob {
  id: number
  name: string
  category: string
  illustration: string
}

export interface RegionJob extends TrendJob {
  rank: number
}

export interface RegionJobs {
  region: string
  jobs: RegionJob[]
}

export interface Mentor {
  id: number
  name: string
  job: string
  profileImage: string
  company?: string
  experience?: number
  location?: string
  meetingType?: 'online' | 'offline' | 'both'
  personality?: string
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

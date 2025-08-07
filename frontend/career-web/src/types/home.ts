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
}

export interface IndustryNews {
  id: number
  category: string
  title: string
  summary: string
  imageUrl: string
}

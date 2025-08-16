export interface PositionResponsibility {
  id: number
  name: string
  imageUrl: string
}

export interface DefaultJob {
  id: number
  name: string
  imageUrl: string
  category: string
}

export interface JobDetail extends DefaultJob {
  trendRank: number
  description: string
  industryTrends: string
  positionResponsibilities: PositionResponsibility[]
}

export interface RegionJob {
  region: string
  positions: DefaultJob[]
}

export interface PositionResponsibility {
  id: number
  name: string
  imageUrl: string
}

export interface DefaultPosition {
  id: number
  name: string
  imageUrl: string
  category: string
}

export interface PositionDetail extends DefaultPosition {
  trendRank: number
  description: string
  industryTrends: string
  positionResponsibilities: PositionResponsibility[]
}

export interface ProvincePosition {
  province: string
  city?: string
  positions: DefaultPosition[]
}

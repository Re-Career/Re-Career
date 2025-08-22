import {
  PositionDetail,
  DefaultPosition,
  RegionPosition,
} from '@/types/position'
import { fetchUrl } from './api'
import { ONE_DAY } from '@/lib/constants/global'

export const getTrendPositions = async (): Promise<DefaultPosition[]> => {
  const res = await fetchUrl('/positions/trend-20', {
    next: { revalidate: ONE_DAY },
  })
  const { data } = await res.json()

  return data
}

export const getPositionDetail = async (
  id: string
): Promise<PositionDetail> => {
  const res = await fetchUrl(`/positions/${id}`)
  const { data } = await res.json()

  return data
}

export const getPositionByRegion = async ({
  provinceId,
  cityId,
}: {
  provinceId: number
  cityId?: number
}): Promise<RegionPosition> => {
  const path = cityId
    ? `provinceId=${provinceId}&cityId=${cityId}`
    : `provinceId=${provinceId}`
  const res = await fetchUrl(`/positions/by-region?${path}`, {
    next: { revalidate: ONE_DAY },
  })
  const { data } = await res.json()

  return data
}

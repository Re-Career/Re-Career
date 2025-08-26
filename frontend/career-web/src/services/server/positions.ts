import {
  PositionDetail,
  DefaultPosition,
  ProvincePosition,
} from '@/types/position'
import { fetchUrl } from '../api'
import { ONE_DAY } from '@/lib/constants/global'
import { FetchResponse } from '@/types/global'

export const getTrendPositions = async (): Promise<
  FetchResponse<DefaultPosition[]>
> => {
  return await fetchUrl<DefaultPosition[]>('/positions/trend-20', {
    next: { revalidate: ONE_DAY, tags: ['positions', 'trend'] },
  })
}

export const getPositionDetail = async (
  id: string
): Promise<FetchResponse<PositionDetail>> => {
  return await fetchUrl<PositionDetail>(`/positions/${id}`, {
    next: { tags: ['positions', `position-${id}`] },
  })
}

export const getPositionsByProvince = async ({
  provinceId,
  cityId,
}: {
  provinceId: number
  cityId?: number
}): Promise<FetchResponse<ProvincePosition>> => {
  const path = cityId
    ? `provinceId=${provinceId}&cityId=${cityId}`
    : `provinceId=${provinceId}`

  return await fetchUrl<ProvincePosition>(`/positions/by-province?${path}`, {
    next: { revalidate: ONE_DAY, tags: ['positions', 'province'] },
  })
}

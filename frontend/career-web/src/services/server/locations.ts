import { City, Province, UserLocation } from '@/types/location'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'

export const getProvinces = async (): Promise<FetchResponse<Province[]>> => {
  return await fetchUrl<Province[]>('/locations/provinces', {
    cache: 'force-cache',
    next: { tags: ['locations', 'provinces'] },
  })
}

export const getCities = async (): Promise<FetchResponse<City[]>> => {
  return await fetchUrl<City[]>('/locations/cities', {
    cache: 'force-cache',
    next: { tags: ['locations', 'cities'] },
  })
}

export const getUserLocation = async (
  location: string
): Promise<UserLocation> => {
  const { longitude, latitude } = JSON.parse(location)

  const response = await fetch(
    `/api/user-location-positions?longitude=${longitude}&latitude=${latitude}`
  )

  if (!response.ok) {
    throw new Error('위치 기반 직업 정보를 가져올 수 없습니다')
  }

  return response.json()
}

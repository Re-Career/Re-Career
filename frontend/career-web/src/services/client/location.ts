import { ProvincePosition } from '@/types/position'

export const fetchUserLocationPositions = async (
  location: string
): Promise<ProvincePosition> => {
  const { longitude, latitude } = JSON.parse(location)

  const response = await fetch(
    `/api/user-location-positions?longitude=${longitude}&latitude=${latitude}`
  )

  if (!response.ok) {
    throw new Error('위치 기반 직업 정보를 가져올 수 없습니다')
  }

  return response.json()
}

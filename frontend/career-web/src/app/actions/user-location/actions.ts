'use server'

import { getKoreanAddress, KoreanAddress } from '@/utils/geolocation'

export const getCurrentLocation = async (
  latitude: number,
  longitude: number
): Promise<KoreanAddress> => {
  const kakaoApiKey = process.env.KAKAO_API_KEY

  const address = await getKoreanAddress(latitude, longitude, kakaoApiKey ?? '')

  return address
}

import { getKoreanAddress, KoreanAddress } from '@/utils/geolocation'

export const getCurrentLocation = async (
  latitude: number,
  longitude: number
): Promise<KoreanAddress> => {
  const kakaoApiKey = process.env.NEXT_PUBLIC_KAKAO_API_KEY

  const address = await getKoreanAddress(latitude, longitude, kakaoApiKey ?? '')

  return address
}

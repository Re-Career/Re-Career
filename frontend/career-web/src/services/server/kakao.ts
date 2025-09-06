export type UserLocationResponse = {
  province: string
  city: string
}

export const getKakaoAddress = async (
  longitude: string,
  latitude: string
): Promise<UserLocationResponse> => {
  const res = await fetch(
    `${process.env.KAKAO_GEO_URL}?x=${longitude}&y=${latitude}`,
    {
      headers: { Authorization: `KakaoAK ${process.env.KAKAO_API_KEY}` },
      cache: 'no-store',
    }
  )

  if (!res.ok) {
    const body = await res.text()

    throw new Error(`Kakao API 실패: ${body}`)
  }

  const data = await res.json()
  const doc = data?.documents?.[0]
  const province =
    doc?.address?.region_1depth_name ??
    doc?.road_address?.region_1depth_name ??
    ''
  const city =
    doc?.address?.region_2depth_name ??
    doc?.road_address?.region_2depth_name ??
    ''

  return { province, city }
}

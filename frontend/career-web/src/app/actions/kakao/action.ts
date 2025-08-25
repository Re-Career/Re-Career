'use server'

export type UserLocationResponse = {
  province: string
  city: string
}

export async function getKakaoAddress(
  longitude: string,
  latitude: string
): Promise<UserLocationResponse> {
  const res = await fetch(
    `https://dapi.kakao.com/v2/local/geo/coord2address.json?x=${longitude}&y=${latitude}`,
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

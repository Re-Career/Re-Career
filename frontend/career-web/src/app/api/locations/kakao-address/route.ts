import { NextResponse } from 'next/server'

export type UserLocationResponse = {
  province: string
  city: string
}

export async function GET(req: Request) {
  const { searchParams } = new URL(req.url)
  const x = searchParams.get('x')
  const y = searchParams.get('y')

  if (!x || !y) {
    return NextResponse.json<{ error: string }>(
      { error: 'x,y 필요' },
      { status: 400 }
    )
  }

  const res = await fetch(
    `https://dapi.kakao.com/v2/local/geo/coord2address.json?x=${x}&y=${y}`,
    {
      headers: { Authorization: `KakaoAK ${process.env.KAKAO_API_KEY}` },
      cache: 'no-store',
    }
  )

  if (!res.ok) {
    const body = await res.text()

    return NextResponse.json<{ error: string; body: string }>(
      { error: 'Kakao API 실패', body },
      { status: res.status }
    )
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

  const result: UserLocationResponse = { province, city }

  return NextResponse.json<UserLocationResponse>(result, { status: 200 })
}

import { NextRequest, NextResponse } from 'next/server'
import { getPositionsByProvince } from '@/services/server/positions'
import { getCities, getProvinces } from '@/services/server/locations'
import { getKakaoAddress } from '@/app/actions/kakao/action'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const longitude = searchParams.get('longitude')
    const latitude = searchParams.get('latitude')

    if (!longitude || !latitude) {
      return NextResponse.json(
        { error: '위도와 경도가 필요합니다' },
        { status: 400 }
      )
    }

    const [
      { data: provinces },
      { data: cities },
      address
    ] = await Promise.all([
      getProvinces(),
      getCities(),
      getKakaoAddress(longitude, latitude),
    ])

    if (!provinces || !cities) {
      return NextResponse.json(
        { error: '지역 정보를 가져올 수 없습니다' },
        { status: 500 }
      )
    }

    // 현재 위치의 province와 매칭되는 province 찾기
    const matchedProvince = provinces.find(
      (p) =>
        address.province.includes(p.name) ||
        p.name.includes(address.province.replace(/특별시|광역시|도$/g, ''))
    )

    // 현재 위치의 city와 매칭되는 city 찾기
    const matchedCity = cities.find(
      (c) =>
        address.city.includes(c.name) ||
        c.name.includes(address.city.replace(/시|구|군$/g, ''))
    )

    if (!matchedProvince || !matchedCity) {
      return NextResponse.json(
        { error: '위치를 찾을 수 없습니다' },
        { status: 404 }
      )
    }

    // 위치 기반 직업 정보 가져오기
    const { data: positionData } = await getPositionsByProvince({
      provinceId: matchedProvince.id,
      cityId: matchedCity.id,
    })

    if (!positionData) {
      return NextResponse.json(
        { error: '위치 기반 직업 정보를 가져올 수 없습니다' },
        { status: 500 }
      )
    }

    return NextResponse.json(positionData)
  } catch (error) {
    console.error('Error fetching user location positions:', error)

    return NextResponse.json(
      { error: '위치 기반 직업 정보를 가져오는데 실패했습니다' },
      { status: 500 }
    )
  }
}

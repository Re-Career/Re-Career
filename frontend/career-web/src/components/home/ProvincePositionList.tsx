'use client'

import React, { useEffect, useState } from 'react'
import { getPositionsByProvince } from '@/services/server/positions'
import { getCurrentPosition } from '@/utils/geolocation'
import Link from 'next/link'
import Image from 'next/image'
import { ProvincePosition } from '@/types/position'
import { UserLocationResponse } from '@/app/api/locations/kakao-address/route'
import { City, Province } from '@/types/location'

interface ProvincePositionListProps {
  positionsByProvince: ProvincePosition
}

const ProvincePositionList = ({
  positionsByProvince,
}: ProvincePositionListProps) => {
  const [data, setData] = useState<ProvincePosition>(positionsByProvince)

  const sectionTitle = `${data.city ? `${data.province}  ${data.city}` : data.province}의 주요 직업`

  useEffect(() => {
    const fetchUserLocationPositions = async () => {
      try {
        const { longitude, latitude } = await getCurrentPosition()
        const data = await fetch(
          `/api/locations/kakao-address?x=${longitude}&y=${latitude}`
        )

        const address: UserLocationResponse = await data.json()

        const [provRes, cityRes] = await Promise.all([
          fetch('/api/locations/provinces', { cache: 'no-store' }),
          fetch('/api/locations/cities', { cache: 'no-store' }),
        ])

        const [provinces, cities]: [provinces: Province[], cities: City[]] =
          await Promise.all([provRes.json(), cityRes.json()])

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

        // 사용자 실제 위치 기반으로 데이터 업데이트
        if (matchedProvince && matchedCity) {
          const positionData = await getPositionsByProvince({
            provinceId: matchedProvince.id,
            cityId: matchedCity.id,
          })

          setData(positionData)
        }
      } catch (error) {
        console.warn('Failed to get user location:', error)
      }
    }

    fetchUserLocationPositions()
  }, [])

  return (
    <section>
      <h2 className="section-title">{sectionTitle}</h2>

      <div className="space-y-3 px-4">
        {data.positions.map((position) => (
          <Link
            key={`Province_${position.id}`}
            className="flex items-center gap-4 rounded-lg"
            href={`/position-detail/${position.id}`}
          >
            <div className="h-12 w-12">
              <Image
                src={position.imageUrl}
                alt={position.name}
                width={48}
                height={48}
                className="rounded-lg object-cover"
              />
            </div>

            <div className="min-w-0 flex-1">
              <h4 className="truncate font-medium text-neutral-900">
                {position.name}
              </h4>
              <p className="text-sm text-gray-600">{position.category}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  )
}

export default ProvincePositionList

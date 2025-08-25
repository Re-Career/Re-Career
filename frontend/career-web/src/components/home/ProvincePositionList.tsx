'use client'

import React, { useEffect, useState } from 'react'
import useSWR from 'swr'
import { getCurrentPosition } from '@/utils/geolocation'
import Link from 'next/link'
import Image from 'next/image'
import { setCookie } from '@/app/actions/global/action'
import { fetchUserLocationPositions } from '@/services/client/location'

const ProvincePositionList = ({
  cachedLocation,
}: {
  cachedLocation?: string
}) => {
  const [location, setLocation] = useState<string>('')

  const key = location ? location : cachedLocation

  const { data, error, isLoading } = useSWR(
    key ? ['user-location-positions', key] : null,
    ([, locationData]) => fetchUserLocationPositions(locationData),
    {
      revalidateOnFocus: false,
      dedupingInterval: 600000,
      errorRetryCount: 1,
    }
  )

  useEffect(() => {
    const initializeLocation = async () => {
      try {
        const currentPosition = await getCurrentPosition()
        const currentLocationString = JSON.stringify(currentPosition)

        if (cachedLocation && cachedLocation !== currentLocationString) {
          await setCookie({
            name: 'location',
            value: currentLocationString,
            options: { httpOnly: false },
          })

          setLocation(currentLocationString)
        }
      } catch {
        if (cachedLocation) {
          setLocation(cachedLocation)
        }
      }
    }

    initializeLocation()
  }, [])

  const sectionTitle = data
    ? `${data.city ? `${data.province}  ${data.city}` : data.province}의 주요 직업`
    : '위치 기반 주요 직업'

  return (
    <section>
      {isLoading ? (
        <div className="mb-4 h-6 w-48 animate-pulse rounded bg-gray-200" />
      ) : (
        <h2 className="section-title">{sectionTitle}</h2>
      )}

      <div className="space-y-3 px-4">
        {isLoading ? (
          // 스켈레톤 UI
          Array.from({ length: 5 }).map((_, index) => (
            <div key={index} className="flex items-center gap-4 rounded-lg">
              <div className="h-12 w-12 animate-pulse rounded-lg bg-gray-200" />
              <div className="min-w-0 flex-1">
                <div className="mb-2 h-4 animate-pulse rounded bg-gray-200" />
                <div className="h-3 w-1/2 animate-pulse rounded bg-gray-200" />
              </div>
            </div>
          ))
        ) : data ? (
          data.positions.map((position) => (
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
          ))
        ) : error ? (
          <div className="py-4 text-center text-gray-500">
            위치 정보를 가져올 수 없습니다
          </div>
        ) : null}
      </div>
    </section>
  )
}

export default ProvincePositionList

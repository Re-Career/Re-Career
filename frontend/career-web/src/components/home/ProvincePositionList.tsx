'use client'

import useSWR from 'swr'
import Link from 'next/link'

import { UserLocation } from '@/types/location'
import { getPositionsByProvince } from '@/services/server/positions'
import { FixedSizeImage } from '../common'
import { useToast } from '@/hooks/useToast'
import { ProvincePosition } from '@/types/position'

interface ProvincePositionListProps {
  userLocationsInfo?: UserLocation
  defaultTrendPositions?: ProvincePosition
}

const ProvincePositionList = ({
  userLocationsInfo,
  defaultTrendPositions,
}: ProvincePositionListProps) => {
  const { showError } = useToast()

  const { data } = useSWR(
    userLocationsInfo ? ['user-location-positions', userLocationsInfo] : null,
    ([, locationData]) => getPositionsByProvince(locationData),
    {
      revalidateOnFocus: false,
      dedupingInterval: 600000,
      errorRetryCount: 1,
    }
  )
  const trendPositions = data?.data || defaultTrendPositions

  if (data?.errorMessage) {
    showError(data?.errorMessage)

    return <></>
  }

  if (!trendPositions) {
    return (
      <section>
        <div className="mx-4 mb-4 h-6 w-48 animate-pulse rounded bg-gray-200" />
        <div className="space-y-3 px-4">
          {Array.from({ length: 5 }).map((_, index) => (
            <div key={index} className="flex items-center gap-4 rounded-lg">
              <div className="h-12 w-12 animate-pulse rounded-lg bg-gray-200" />
              <div className="min-w-0 flex-1">
                <div className="mb-2 h-4 animate-pulse rounded bg-gray-200" />
                <div className="h-3 w-1/2 animate-pulse rounded bg-gray-200" />
              </div>
            </div>
          ))}
        </div>
      </section>
    )
  }

  const { city, province, positions } = trendPositions

  return (
    <section>
      <h2 className="section-title">
        {city ? `${province}  ${city}` : province}의 주요 직업
      </h2>

      <div className="space-y-3 px-4">
        {positions.map((position) => (
          <Link
            key={`Province_${position.id}`}
            className="flex items-center gap-4 rounded-lg"
            href={`/position-detail/${position.id}`}
          >
            <FixedSizeImage
              src={position.imageUrl}
              alt={`province_position_image_${position.name}`}
              size="sm"
            />
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

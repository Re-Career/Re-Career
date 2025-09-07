'use client'

import { setCookie } from '@/app/actions/global/action'
import { getCurrentPosition } from '@/lib/utils/geolocation'
import { useEffect, useMemo, useState } from 'react'
import ProvincePositionList from './ProvincePositionList'
import MentorList from './MentorList'
import useSWR from 'swr'
import { getUserLocation } from '@/services/server/locations'
import { ProvincePosition } from '@/types/position'
import { Mentor } from '@/types/mentor'

const RelatedLocationGroup = ({
  cachedLocation,
  defaultTrendPositions,
  defaultMentors,
}: {
  cachedLocation?: string
  defaultTrendPositions?: ProvincePosition
  defaultMentors?: Mentor[]
}) => {
  const [location, setLocation] = useState<string>('')

  const key = location ? location : cachedLocation

  const { data } = useSWR(
    key ? ['user-location-positions', key] : null,
    ([, locationData]) => getUserLocation(locationData),
    {
      revalidateOnFocus: false,
      dedupingInterval: 600000,
      errorRetryCount: 1,
    }
  )

  const userLocationsInfo = useMemo(() => {
    if (data) {
      return data
    }
  }, [data])

  useEffect(() => {
    const initializeLocation = async () => {
      try {
        const currentPosition = await getCurrentPosition()
        const currentLocationString = JSON.stringify(currentPosition)

        if (!cachedLocation || cachedLocation !== currentLocationString) {
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

  return (
    <>
      <ProvincePositionList
        userLocationsInfo={userLocationsInfo}
        defaultTrendPositions={defaultTrendPositions}
      />
      <MentorList
        provinceId={userLocationsInfo?.provinceId}
        defaultMentors={defaultMentors}
      />
    </>
  )
}

export default RelatedLocationGroup

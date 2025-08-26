'use client'

import { setCookie } from '@/app/actions/global/action'
import { getCurrentPosition } from '@/utils/geolocation'
import { useEffect, useMemo, useState } from 'react'
import ProvincePositionList from './ProvincePositionList'
import MentorList from './MentorList'
import useSWR from 'swr'
import { getUserLocation } from '@/services/server/locations'

const RelatedLocationGroup = ({
  cachedLocation,
}: {
  cachedLocation?: string
}) => {
  const [location, setLocation] = useState<string>('')

  const key = location ? location : cachedLocation

  const { data, error } = useSWR(
    key ? ['user-location-positions', key] : null,
    ([, locationData]) => getUserLocation(locationData),
    {
      revalidateOnFocus: false,
      dedupingInterval: 600000,
      errorRetryCount: 1,
    }
  )

  const locationsIds = useMemo(() => {
    if (error) {
      return { provinceId: 1 }
    }

    if (data) {
      return data
    }
  }, [data, error])

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
      <ProvincePositionList locationsIds={locationsIds} />
      <MentorList provinceId={locationsIds?.provinceId} />
    </>
  )
}

export default RelatedLocationGroup

'use client'

import HorizontalScroll from '@/components/common/HorizontalScroll'
import Link from 'next/link'
import Image from 'next/image'
import { getMentors } from '@/services/server/mentor'
import useSWR from 'swr'

interface MentorListProps {
  provinceId?: number
}

const MentorList = ({ provinceId }: MentorListProps) => {
  const { data } = useSWR(
    provinceId ? ['user-location-positions', provinceId] : null,
    ([, provinceId]) => getMentors(provinceId),
    {
      revalidateOnFocus: false,
      dedupingInterval: 600000,
      errorRetryCount: 1,
    }
  )

  if (data?.errorMessage) {
    return <></>
  }

  if (!data) {
    return (
      <section className="">
        <div>
          <div className="mx-4 mb-4 h-6 w-48 animate-pulse rounded bg-gray-200" />
          <div className="flex gap-4 overflow-x-auto px-4">
            {Array.from({ length: 4 }).map((_, index) => (
              <div
                key={index}
                className="flex min-w-32 flex-col items-center gap-3"
              >
                <div className="h-32 w-32 animate-pulse rounded-lg bg-gray-200" />
                <div className="text-center">
                  <div className="mb-2 h-4 animate-pulse rounded bg-gray-200" />
                  <div className="h-3 w-1/2 animate-pulse rounded bg-gray-200" />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    )
  }

  const { data: mentors } = data

  return (
    <section className="">
      <div>
        <h2 className="section-title">당신을 위한 멘토들</h2>

        <HorizontalScroll>
          {mentors?.map((mentor) => (
            <Link
              key={mentor.id}
              className="flex flex-col items-center gap-3"
              href={`/mentor/${mentor.id}/profile`}
            >
              <div className="h-32 w-32">
                <Image
                  src={mentor.profileImageUrl}
                  alt={`mentor_by_region_${mentor.id}`}
                  width={128}
                  height={128}
                  className="h-full w-full rounded-lg object-cover object-top"
                />
              </div>

              <div className="text-center">
                <h3 className="font-medium text-neutral-900">{mentor.name}</h3>
                <p className="text-sm text-gray-600">{mentor.position.name}</p>
              </div>
            </Link>
          ))}
        </HorizontalScroll>
      </div>
    </section>
  )
}

export default MentorList

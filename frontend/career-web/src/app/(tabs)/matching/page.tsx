'use client'

import { HorizontalScroll } from '@/components/common'
import { Header, PageWithHeader } from '@/components/layout'
import Filter from '@/components/matching/Filter'
import { getFilteredMentors } from '@/services/mentor'
import Image from 'next/image'
import Link from 'next/link'
import { useSearchParams } from 'next/navigation'
import useSWR from 'swr'

const MatchingPage = () => {
  const searchParams = useSearchParams()

  const mentorName = searchParams.get('mentorName') || ''
  const filters: Record<string, string[]> = {}

  searchParams.forEach((value, key) => {
    if (key !== 'search' && value) {
      filters[key] = value.split(',')
    }
  })

  const { data, error, isLoading } = useSWR(
    ['filtered-mentors', mentorName, filters],
    () => getFilteredMentors({ mentorName, filters }),
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      dedupingInterval: 300000, // 5분
      focusThrottleInterval: 300000, // 5분
    }
  )

  const recommendedMentors = data?.primary || []
  const searchedMentors = data?.secondary || []

  if (error) {
    return (
      <>
        <Header title="멘토 찾기" />
        <PageWithHeader className="pt-14">
          <Filter
            initialFilterConfigs={filters}
            initialMentorName={mentorName}
          />
          <div className="flex items-center justify-center py-8">
            <p className="text-gray-500">
              데이터를 불러오는 중 오류가 발생했습니다.
            </p>
          </div>
        </PageWithHeader>
      </>
    )
  }

  return (
    <>
      <Header title="멘토 찾기" />

      <PageWithHeader className="pt-14">
        {/* 검색 섹션 */}
        <Filter initialFilterConfigs={filters} initialMentorName={mentorName} />

        {isLoading ? (
          <div className="flex flex-col items-center justify-center py-8">
            <div className="border-t-primary-500 h-8 w-8 animate-spin rounded-full border-4 border-gray-300"></div>
            <p className="mt-2 text-gray-500">멘토를 검색하는 중...</p>
          </div>
        ) : (
          <>
            {/* 추천 멘토 리스트 */}
            {recommendedMentors.length > 0 && (
              <div className="mb-4 border-b border-gray-100 pb-4">
                <h2 className="mb-4 px-4 text-lg font-bold text-gray-900">
                  추천 매칭
                </h2>
                <HorizontalScroll>
                  {recommendedMentors.map((mentor) => (
                    <Link
                      href={`mentor/${mentor.id}/profile`}
                      key={mentor.id}
                      className="flex-shrink-0 cursor-pointer rounded-lg bg-white"
                    >
                      <div className="mb-3 h-40 w-40">
                        <Image
                          src={mentor.profileImageUrl}
                          alt={mentor.name}
                          width={160}
                          height={160}
                          className="h-full w-full rounded-lg object-cover object-top"
                        />
                      </div>
                      <div>
                        <h3 className="mb-1 font-semibold text-gray-900">
                          {mentor.name}
                        </h3>
                        <p className="mb-1 text-sm text-gray-600">
                          {mentor.position.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          {mentor.company?.name ?? ''} • {mentor.experience}년
                        </p>
                      </div>
                    </Link>
                  ))}
                </HorizontalScroll>
              </div>
            )}
            {/* 전체 멘토 리스트 */}
            <div>
              <div className="mb-4 flex items-center gap-2 px-4">
                <h2 className="text-lg font-bold text-gray-900">검색 결과</h2>
                {searchedMentors.length > 0 && (
                  <span className="text-sm">총 {searchedMentors.length}건</span>
                )}
              </div>
              <div className="px-4">
                <div className="space-y-5">
                  {searchedMentors.length > 0 ? (
                    searchedMentors.map((mentor) => (
                      <div key={`mentor_${mentor.id}`}>
                        <div className="flex items-center gap-4">
                          <div className="h-14 w-14 flex-shrink-0 overflow-hidden rounded-full">
                            <Image
                              src={mentor.profileImageUrl}
                              alt={mentor.name}
                              width={56}
                              height={56}
                              className="h-full w-full object-cover object-top"
                            />
                          </div>

                          <div className="flex-1">
                            <div className="flex items-center justify-between">
                              <div>
                                <h3 className="font-semibold text-gray-900">
                                  {mentor.name}
                                  <span className="ml-1 text-sm text-gray-500">
                                    {mentor.position.name}
                                  </span>
                                </h3>

                                <p className="text-xs text-gray-900">
                                  {mentor.company?.name ?? ''} •{' '}
                                  {mentor.experience}년 •{mentor.province.name}
                                </p>
                                <p className="text-xs text-gray-900">
                                  {mentor.personalityTags
                                    ?.map((tag) => tag.name)
                                    .join(', ')}
                                </p>
                              </div>

                              <Link
                                href={`mentor/${mentor.id}/profile`}
                                className="bg-primary-500 flex-shrink-0 rounded-xl px-4 py-1.5 text-sm"
                              >
                                1:1 예약
                              </Link>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p>검색결과가 없습니다.🥲</p>
                  )}
                </div>
              </div>
            </div>
          </>
        )}
      </PageWithHeader>
    </>
  )
}

export default MatchingPage

import React from 'react'
import Image from 'next/image'
import Header from '@/components/common/Header'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import Filter from '@/components/matching/Filter'
import { getFilteredMenters, getRecommenedMentors } from '@/services/mentor'
import PageWithHeader from '@/components/ui/PageWithHeader'

interface MatchingPageProps {
  searchParams: Promise<Record<string, string>>
}

const MatchingPage = async ({ searchParams }: MatchingPageProps) => {
  const params = await searchParams
  const mentorName = params.mentorName || ''

  // URL에서 필터 값 추출 - 동적으로 처리
  const filters: Record<string, string[]> = {}

  Object.entries(params).forEach(([key, value]) => {
    if (key !== 'search' && value) {
      filters[key] = value.split(',')
    }
  })

  const recommendedMentors = getRecommenedMentors()
  const filteredMentors = getFilteredMenters({ mentorName, filters })

  return (
    <>
      <Header title="멘토 찾기" />

      <PageWithHeader>
        {/* 검색 섹션 */}
        <Filter initialFilters={filters} initialMentorName={mentorName} />

        {/* 추천 멘토 리스트 */}
        <div className="border-b border-gray-100 pb-4">
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
                    className="h-full w-full rounded-lg object-cover"
                  />
                </div>
                <div>
                  <h3 className="mb-1 font-semibold text-gray-900">
                    {mentor.name}
                  </h3>
                  <p className="mb-1 text-sm text-gray-600">{mentor.job}</p>
                  <p className="text-xs text-gray-500">
                    {mentor.company} • {mentor.experience}년
                  </p>
                </div>
              </Link>
            ))}
          </HorizontalScroll>
        </div>

        {/* 전체 멘토 리스트 */}
        <div className="px-4">
          <div className="space-y-5">
            {filteredMentors.map((mentor) => (
              <div key={`mentor_${mentor.id}`}>
                <div className="flex items-center gap-4">
                  <div className="h-14 w-14 flex-shrink-0 overflow-hidden rounded-full">
                    <Image
                      src={mentor.profileImageUrl}
                      alt={mentor.name}
                      width={56}
                      height={56}
                      className="h-full w-full object-cover"
                    />
                  </div>

                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold text-gray-900">
                          {mentor.name}
                          <span className="ml-1 text-sm text-gray-500">
                            {mentor.job}
                          </span>
                        </h3>

                        <p className="text-xs text-gray-900">
                          {mentor.company} • {mentor.experience}년 •
                          {mentor.region}
                        </p>
                        <p className="text-xs text-gray-900">
                          {mentor.personalityTags
                            ?.map((tag) => tag.name)
                            .join(', ')}
                        </p>
                      </div>

                      <Link
                        href={`mentor/${mentor.id}/profile`}
                        className="bg-primary flex-shrink-0 rounded-xl px-4 py-1.5 text-sm"
                      >
                        1:1 예약
                      </Link>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </PageWithHeader>
    </>
  )
}

export default MatchingPage

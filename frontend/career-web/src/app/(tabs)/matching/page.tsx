import { FixedSizeImage, HorizontalScroll } from '@/components/common'
import { Header, PageWithHeader } from '@/components/layout'
import Filter from '@/components/matching/Filter'
import { getFilteredMentors } from '@/services/server/mentor'
import Link from 'next/link'

const MatchingPage = async ({
  searchParams,
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>
}) => {
  const params = await searchParams

  const mentorName = (params?.mentorName as string) ?? ''

  const toArray = (v: string | string[] | undefined): string[] => {
    if (!v) return []

    if (Array.isArray(v))
      return v.flatMap((x) => String(x).split(',')).filter(Boolean)

    return String(v).split(',').filter(Boolean)
  }

  // 쿼리 키 전체를 순회해 필터 맵 구성 (search/mentorName 등 제외)
  const filters: Record<string, string[]> = Object.entries(params).reduce(
    (acc, [key, value]) => {
      if (key === 'search' || key === 'mentorName') return acc

      const arr = toArray(value)

      if (arr.length) acc[key] = arr

      return acc
    },
    {} as Record<string, string[]>
  )

  const res = await getFilteredMentors({ mentorName, filters })
  const recommendedList = res?.data?.recommendedList ?? []
  const searchedList = res?.data?.searchedList ?? []

  return (
    <>
      <Header title="멘토 찾기" />

      <PageWithHeader className="pt-14">
        <Filter initialFilterConfigs={filters} initialMentorName={mentorName} />

        {/* 추천 멘토 리스트 */}
        {recommendedList.length > 0 && (
          <div className="mb-4 border-b border-gray-100 pb-4">
            <h2 className="mb-4 px-4 text-lg font-bold text-gray-900">
              추천 매칭
            </h2>
            <HorizontalScroll>
              {recommendedList.map((mentor) => (
                <Link
                  href={`/mentor/${mentor.id}/profile`}
                  key={mentor.id}
                  className="flex-shrink-0 cursor-pointer rounded-lg bg-white pt-3"
                >
                  <FixedSizeImage
                    src={mentor.profileImageUrl}
                    alt={`recommended_mentor_image_${mentor.name}`}
                    size="lg"
                  />
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
            {searchedList.length > 0 && (
              <span className="text-sm">총 {searchedList.length}건</span>
            )}
          </div>
          <div className="px-4">
            <div className="space-y-5">
              {searchedList.length > 0 ? (
                searchedList.map((mentor) => (
                  <div key={`mentor_${mentor.id}`}>
                    <div className="flex items-center gap-4">
                      <FixedSizeImage
                        src={mentor.profileImageUrl}
                        alt={`mentor_image_${mentor.name}`}
                        size="sm"
                      />
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
                              {mentor.company?.name ?? ''} • {mentor.experience}
                              년 • {mentor.province.name}
                            </p>
                            <p className="text-xs text-gray-900">
                              {mentor.personalityTags
                                ?.map((t) => t.name)
                                .join(', ')}
                            </p>
                          </div>
                          <Link
                            href={`/mentor/${mentor.id}/profile`}
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
      </PageWithHeader>
    </>
  )
}

export default MatchingPage

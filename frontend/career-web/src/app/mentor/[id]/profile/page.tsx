import { FixedSizeImage } from '@/components/common'
import { Header, PageWithHeader } from '@/components/layout'
import { getMentor } from '@/services/server/mentor'
import { notFound } from 'next/navigation'
import ReserveButtonWithAuth from '@/components/mentor/profile/ReserveButtonWithAuth'

const renderStars = (rating: number) => {
  return Array.from({ length: 5 }, (_, i) => (
    <span
      key={i}
      className={`text-lg ${i < rating ? 'text-secondary-500' : 'text-gray-300'}`}
    >
      ★
    </span>
  ))
}

const MentorProfilePage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params
  const { data: mentor } = await getMentor(id)

  if (!mentor) {
    notFound()
  }

  return (
    <>
      <Header title="멘토 프로필" showBackButton />
      <PageWithHeader>
        {/* 프로필 상단 */}
        <div className="bg-white p-6 text-center">
          <FixedSizeImage
            src={mentor.profileImageUrl}
            alt={`mentor_image_${mentor.name}`}
            divClassName="mx-auto mb-4"
            size="md"
          />

          <h1 className="mb-1 text-xl font-bold text-gray-900">
            {mentor.name}
          </h1>

          <p className="mb-3 text-gray-600">{mentor.shortDescription}</p>

          <div className="mb-2 flex items-center justify-center gap-2">
            {mentor.feedback && (
              <>
                <div className="flex">
                  {renderStars(Math.floor(mentor.feedback.rating))}
                </div>
                <span className="text-sm text-gray-600">
                  {mentor.feedback.rating} ({mentor.feedback.count}명)
                </span>
              </>
            )}
          </div>

          <div className="text-sm text-gray-500">
            {mentor.company ? `${mentor.company.name} •` : ''}
            경력 {mentor.experience}년 • {mentor.province.name}
          </div>
        </div>

        <div className="space-y-4">
          {/* 소개 */}
          <div className="rounded-lg bg-white p-4">
            <h2 className="mb-3 text-lg font-semibold text-gray-900">
              멘토에 대하여
            </h2>
            <p className="leading-relaxed text-gray-700">
              {mentor.introduction}
            </p>
          </div>

          {/* 주요 기술 */}
          {mentor.skills && mentor.skills.length > 0 && (
            <div className="rounded-lg bg-white p-4">
              <h2 className="mb-3 text-lg font-semibold text-gray-900">
                주요 기술
              </h2>
              <div className="flex flex-wrap gap-2">
                {mentor.skills.map((skill, index) => (
                  <span
                    key={index}
                    className="rounded-full bg-gray-200 px-3 py-1 text-sm text-gray-800"
                  >
                    {skill.name}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* 경력 */}
          {mentor.career && mentor.career.length > 0 && (
            <div className="rounded-lg bg-white p-4">
              <h2 className="mb-3 text-lg font-semibold text-gray-900">경력</h2>
              <div className="space-y-2">
                {mentor.career.map((item, index) => (
                  <div key={index} className="flex items-start gap-3">
                    <div className="mt-2 h-2 w-2 flex-shrink-0 rounded-full bg-gray-700"></div>
                    <p className="text-sm text-gray-700">{item}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 멘토 피드백 */}
          {mentor.feedback?.comments && mentor.feedback.comments.length > 0 && (
            <div className="rounded-lg bg-white p-4">
              <h2 className="mb-3 text-lg font-semibold text-gray-900">
                멘토 피드백
              </h2>

              <div className="mb-4 flex items-center gap-2 rounded-lg bg-gray-50 p-3">
                <div className="text-xl font-bold text-gray-900">
                  {mentor.feedback.rating}
                </div>
                <div className="flex justify-center">
                  {renderStars(Math.floor(mentor.feedback.rating))}
                </div>
                <div className="text-sm text-gray-600">
                  ({mentor.feedback.count}개 리뷰)
                </div>
              </div>

              <div className="space-y-3">
                {mentor.feedback.comments.map((comment) => (
                  <div
                    key={comment.id}
                    className="border-b border-gray-100 pb-3 last:border-b-0"
                  >
                    <div className="mb-2 flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-medium text-gray-900">
                          {comment.user}
                        </span>
                        <div className="flex">
                          {renderStars(comment.rating)}
                        </div>
                      </div>
                      <span className="text-xs text-gray-500">
                        {comment.date}
                      </span>
                    </div>
                    <p className="text-sm text-gray-700">{comment.comment}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* 하단 상담 예약 버튼 */}
        <div className="sticky bottom-0 flex border-t border-gray-100 bg-white p-4">
          <ReserveButtonWithAuth id={id} />
        </div>
      </PageWithHeader>
    </>
  )
}

export default MentorProfilePage

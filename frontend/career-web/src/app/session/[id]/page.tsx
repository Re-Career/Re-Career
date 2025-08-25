import { Header, PageWithHeader } from '@/components/layout'
import { getSession } from '@/services/server/session'
import dayjs from 'dayjs'
import Image from 'next/image'
import Link from 'next/link'
import { notFound, redirect } from 'next/navigation'

const page = async ({ params }: { params: Promise<{ id: string }> }) => {
  const { id } = await params

  const { data, status } = await getSession(id)

  if (status === 401 || status === 403) {
    return redirect('/')
  }

  if (!data) {
    return notFound()
  }

  const { sessionTime, mentor } = data

  return (
    <>
      <Header showBackButton title="상담 세부사항" />
      <PageWithHeader className="space-y-8 px-4">
        <div className="space-y-2">
          <h2 className="text-xl font-bold text-neutral-900">상담 날짜</h2>
          <p>{dayjs(sessionTime).format('YYYY년 MM월 DD일  hh시 mm분')}</p>
        </div>

        <div className="space-y-3">
          <h2 className="text-xl font-bold text-neutral-900">멘토 정보</h2>
          <Link
            className="group block rounded-xl bg-white p-4 shadow-sm ring-1 ring-gray-200/50 transition-all duration-200 hover:shadow-md hover:ring-gray-300/50"
            href={`/mentor/${mentor.mentorId}/profile`}
          >
            <div className="flex items-center gap-4">
              <div className="relative h-16 w-16 flex-shrink-0">
                <Image
                  width={64}
                  height={64}
                  alt="mentor_profile_image"
                  src={mentor.profileImageUrl}
                  className="h-full w-full rounded-xl object-cover object-top ring-2 ring-gray-100"
                />
              </div>
              <div className="flex-1 space-y-1">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold text-gray-900">{mentor.name}</h3>
                  <svg className="h-5 w-5 text-gray-400 group-hover:text-gray-600 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                  </svg>
                </div>
                <p className="text-sm text-gray-600">{mentor.position.name}</p>
                <p className="text-xs text-blue-600">프로필 보기</p>
              </div>
            </div>
          </Link>
        </div>

        <div className="space-y-2">
          <h2 className="text-xl font-bold text-neutral-900">상담 요약</h2>
          <p>
            멘토와 {mentor.position.name} 관련 직무에 대한 관심을 논의했습니다.
            직업 탐색을 위한 잠재적인 경로와 전략을 탐구했습니다. 멘토의
            커리어와 향후 계획에 대해 질의했습니다.
          </p>
        </div>
      </PageWithHeader>
    </>
  )
}

export default page

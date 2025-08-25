import { MentoringDesc } from '@/components/common'
import { Header, PageWithHeader } from '@/components/layout'
import { getSessionList } from '@/services/server/session'
import { SessionStatus } from '@/types/session'
import { notFound } from 'next/navigation'

interface PageProps {
  params: Promise<{ status: string }>
}

const page = async ({ params }: PageProps) => {
  const { status } = await params

  // status 파라미터가 유효한 SessionStatus인지 확인
  const validStatuses = Object.values(SessionStatus)

  if (!validStatuses.includes(status as SessionStatus)) {
    notFound()
  }

  const { data: sessions } = await getSessionList()

  if (!sessions) {
    notFound()
  }

  const statusParam = status as SessionStatus

  // status에 따라 필터링
  let filteredSessions
  let pageTitle

  if (
    statusParam === SessionStatus.REQUESTED ||
    statusParam === SessionStatus.CONFIRMED
  ) {
    // 예정된 상담 (REQUESTED + CONFIRMED)
    filteredSessions = sessions.filter((session) =>
      [SessionStatus.REQUESTED, SessionStatus.CONFIRMED].includes(
        session.status
      )
    )
    pageTitle = '예정된 상담'
  } else {
    // 완료된 상담 (CANCELED + COMPLETED)
    filteredSessions = sessions.filter((session) =>
      [SessionStatus.CANCELED, SessionStatus.COMPLETED].includes(session.status)
    )
    pageTitle = '완료된 상담'
  }

  // 날짜순으로 정렬 (예정된 상담은 오름차순, 완료된 상담은 내림차순)
  const sortedSessions = filteredSessions.sort((a, b) => {
    const timeA = new Date(a.sessionTime).getTime()
    const timeB = new Date(b.sessionTime).getTime()

    if (
      statusParam === SessionStatus.REQUESTED ||
      statusParam === SessionStatus.CONFIRMED
    ) {
      // 예정된 상담: 오름차순 (가까운 날짜부터)
      return timeA - timeB
    } else {
      // 완료된 상담: 내림차순 (최신순)
      return timeB - timeA
    }
  })

  const groupSessionsByMonth = (sessions: typeof sortedSessions) => {
    const groups: { [key: string]: typeof sortedSessions } = {}

    sessions.forEach((session) => {
      const date = new Date(session.sessionTime)
      const monthKey = new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: 'long',
      }).format(date)

      if (!groups[monthKey]) {
        groups[monthKey] = []
      }
      groups[monthKey].push(session)
    })

    return groups
  }

  const groupedSessions = groupSessionsByMonth(sortedSessions)

  // 월별 그룹의 순서를 정렬 (예정된 상담은 오름차순, 완료된 상담은 내림차순)
  const sortedMonthEntries = Object.entries(groupedSessions).sort(
    ([monthA], [monthB]) => {
      const dateA = new Date(monthA.replace(/년|월/g, '').replace(' ', '-01-'))
      const dateB = new Date(monthB.replace(/년|월/g, '').replace(' ', '-01-'))

      if (
        statusParam === SessionStatus.REQUESTED ||
        statusParam === SessionStatus.CONFIRMED
      ) {
        return dateA.getTime() - dateB.getTime() // 오름차순
      } else {
        return dateB.getTime() - dateA.getTime() // 내림차순
      }
    }
  )

  return (
    <>
      <Header title={pageTitle} showBackButton />
      <PageWithHeader className="space-y-6 px-4 pb-4">
        {sortedMonthEntries.map(([month, sessions]) => (
          <div key={month} className="space-y-4">
            <h2 className="text-lg font-semibold text-gray-800">{month}</h2>
            <div className="space-y-3">
              {sessions.map((session) => (
                <MentoringDesc key={session.id} session={session} />
              ))}
            </div>
          </div>
        ))}
      </PageWithHeader>
    </>
  )
}

export default page

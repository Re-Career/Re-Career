import { Header, PageWithHeader } from '@/components/layout'
import { getSession } from '@/services/server/session'
import { notFound, redirect } from 'next/navigation'
import dayjs from 'dayjs'

const columns = [
  { key: 'date' as const, name: '날짜' },
  { key: 'time' as const, name: '시간' },
  { key: 'mentorName' as const, name: '멘토' },
] as const

const ReservationSuccessPage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params

  const { data: session, status } = await getSession(id)

  if (status === 401 || status === 403) {
    return redirect('/')
  }

  if (!session) {
    return notFound()
  }

  const mentorName = session.mentor.name

  const getData = (key: string): string => {
    const sessionDate = dayjs(session.sessionTime)

    switch (key) {
      case 'mentorName':
        return mentorName
      case 'date':
        return sessionDate.format('YYYY년 MM월 DD일')
      case 'time':
        return sessionDate.format('HH:mm')
      default:
        return ''
    }
  }

  return (
    <>
      <Header title="예약 확인" showBackButton />
      <PageWithHeader>
        <section className="space-y-4 px-4 pt-4">
          <h2 className="text-center text-2xl font-bold">상담 예약 완료</h2>
          <div>
            <p>{mentorName}님과의 상담이 준비되었습니다.</p>
            <p>모든 세부 사항이 포함된 확인 이메일을 발송했습니다.</p>
          </div>
        </section>

        <section className="px-4">
          <h4 className="py-4 text-lg font-bold">섹션 세부 사항</h4>
          {columns.map(({ key, name }) => (
            <div key={key} className="border-t border-gray-200 py-5">
              <div className="text-gray-600">{name}</div>
              <div>{getData(key)}</div>
            </div>
          ))}
          <h4 className="py-4 text-lg font-bold">참여 방법</h4>
          <p>
            상담 시작 15분 전에 영상 통화에 참여할 수 있는 링크가 귀하의 이메일
            주소로 전송됩니다. 안정적인 인터넷 연결과 조용한 환경을 확보해
            주시기 바랍니다.
          </p>
          <h4 className="py-4 text-lg font-bold">일정변경 가능 시간 </h4>
          <p>상담 24시간전까지</p>
        </section>
      </PageWithHeader>
    </>
  )
}

export default ReservationSuccessPage

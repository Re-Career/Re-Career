import { Header, PageWithHeader } from '@/components/layout'

const columns = [
  { key: 'date' as const, name: '날짜' },
  { key: 'time' as const, name: '시간' },
  { key: 'name' as const, name: '멘토' },
  { key: 'type' as const, name: '형식' },
] as const

const page = () => {
  const data = {
    date: '2024-07-22',
    time: '14:00',
    name: '김지원',
    type: '영상 통화',
  }

  return (
    <>
      <Header title="예약 확인" showBackButton />
      <PageWithHeader>
        <section className="space-y-4 px-4 pt-4">
          <h2 className="text-center text-2xl font-bold">상담 예약 완료</h2>
          <p>
            김지원님과의 상담이 준비되었습니다. 모든 세부 사항이 포함된 확인
            이메일을 발송했습니다.
          </p>
        </section>

        <section className="px-4">
          <h4 className="py-4 text-lg font-bold">섹션 세부 사항</h4>
          {columns.map((column) => (
            <div key={column.key} className="border-t border-gray-200 py-5">
              <div className="text-gray-600">{column.name}</div>
              <div>{data[column.key]}</div>
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

export default page

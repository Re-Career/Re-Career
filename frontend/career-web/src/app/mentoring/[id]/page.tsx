import Header from '@/components/common/Header'
import Image from 'next/image'
import React from 'react'

const page = async ({ params }: { params: Promise<{ id: string }> }) => {
  await params

  return (
    <>
      <Header showBackButton title="상담 세부사항" />
      <h2 className="section-title">상담 날짜</h2>
      <div className="flex justify-between px-4">
        <p>2024년 7월 20일 </p>
        <p>오전 10:00</p>
      </div>
      <h2 className="section-title">상담 날짜</h2>
      <div className="flex items-center gap-4 px-4">
        <Image width={56} height={56} alt="" src="" className="rounded-full" />
        <div>
          <p>올리비아 카터</p>
          <p>프로덕트 디자이너</p>
        </div>
      </div>
      <h2 className="section-title">상담 요약</h2>
      <p className="px-4">
        올리비아와 사라는 데이터 관련 직무에 대한 사라의 관심을 논의했습니다.
        올리비아는 사라의 구체적인 관심 분야에 대해 질문했고, 사라는 데이터 관련
        직무에 집중하고 있다고 확인했습니다. 그들은 사라의 직업 탐색을 위한
        잠재적인 경로와 전략을 탐구했습니다.
      </p>
      <h2 className="section-title">전체 상담 내역</h2>
    </>
  )
}

export default page

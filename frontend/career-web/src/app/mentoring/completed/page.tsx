import Header from '@/components/common/Header'
import MentoringDesc from '@/components/common/MentoringDesc'
import React from 'react'

const page = () => {
  return (
    <>
      <Header title="완료된 상담" showBackButton />
      <h4 className="section-title">이번달</h4>
      <div className="px-4">
        <MentoringDesc />
      </div>
    </>
  )
}

export default page

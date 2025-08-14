import Image from 'next/image'
import Link from 'next/link'
import React from 'react'

const MentoringDesc = () => {
  return (
    <Link className="flex items-center gap-4" href="/mentoring/1">
      <Image className="h-14 w-14" alt="" src="" />
      <div>
        <div className="flex gap-1">
          <p>김지원</p>|<p>Product Designer</p>
        </div>
        <p>2025-07-12 15:30</p>
      </div>
    </Link>
  )
}

export default MentoringDesc

import React from 'react'
import { mentors } from '@/mocks/home/mentor-list'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import Link from 'next/link'
import Image from 'next/image'

const MentorList = () => {
  return (
    <section className="">
      <div>
        <h2 className="section-title">당신을 위한 멘토들</h2>

        <HorizontalScroll>
          {mentors.map((mentor) => (
            <Link
              key={mentor.id}
              className="flex flex-col items-center gap-3"
              href={`mentor/${mentor.id}/profile`}
            >
              <div className="h-32 w-32 flex-shrink-0 overflow-hidden rounded-full">
                <Image
                  src={mentor.profileImage}
                  alt={mentor.name}
                  width={128}
                  height={128}
                  className="h-full w-full object-cover"
                />
              </div>

              <div className="text-center">
                <h3 className="font-medium text-neutral-900">{mentor.name}</h3>
                <p className="text-sm text-gray-600">{mentor.job}</p>
              </div>
            </Link>
          ))}
        </HorizontalScroll>
      </div>
    </section>
  )
}

export default MentorList

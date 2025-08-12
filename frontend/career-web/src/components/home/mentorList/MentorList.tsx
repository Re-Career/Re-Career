import React from 'react'
import { mentors } from '@/mocks/home/mentor-list'
import MentorButton from './MentorButton'

const MentorList = () => {
  return (
    <section className="">
      <div>
        <h2 className="px-4 pt-5 pb-3 text-xl font-bold text-neutral-900">
          당신을 위한 멘토들
        </h2>

        <div className="flex gap-4 overflow-x-auto p-4">
          {mentors.map((mentor) => (
            <MentorButton key={`mentor_list_${mentor.id}`} mentor={mentor} />
          ))}
        </div>
      </div>
    </section>
  )
}

export default MentorList

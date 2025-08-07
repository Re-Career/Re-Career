'use client'

import React from 'react'
import Image from 'next/image'
import { mentors } from '@/mocks/home/mentor-list'
import { useRouter } from 'next/navigation'

const MentorList = () => {
  const router = useRouter()

  const handleMentorProfile = (id: number) => {
    if (window.ReactNativeWebView) {
      window.ReactNativeWebView.postMessage(
        JSON.stringify({
          type: 'MENTOR_PROFILE',
          data: { mentorId: id },
        })
      )
    } else {
      router.push(`/profile/mentor/${id}`)
    }
  }

  return (
    <section className="">
      <div>
        <h2 className="px-4 pt-5 pb-3 text-xl font-bold text-neutral-900">
          당신을 위한 멘토들
        </h2>

        <div className="flex gap-4 overflow-x-auto p-4">
          {mentors.map((mentor) => (
            <button
              key={mentor.id}
              className="flex flex-col items-center gap-3"
              onClick={() => handleMentorProfile(mentor.id)}
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
            </button>
          ))}
        </div>
      </div>
    </section>
  )
}

export default MentorList

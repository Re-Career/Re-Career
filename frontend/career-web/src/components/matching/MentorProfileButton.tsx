'use client'

import { useRouter } from 'next/navigation'
import React from 'react'

const MentorProfileButton = ({ id }: { id: number }) => {
  const router = useRouter()

  const handleMentorProfile = () => {
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
    <button
      onClick={handleMentorProfile}
      className="bg-primary flex-shrink-0 rounded-xl px-4 py-1.5 text-sm"
    >
      1:1 예약
    </button>
  )
}

export default MentorProfileButton

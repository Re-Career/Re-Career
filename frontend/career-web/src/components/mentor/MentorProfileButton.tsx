'use client'

import { WebViewMessageTypes } from '@/lib/constants/global'
import { isWebView, sendMessageToNative } from '@/utils/webview'
import { useRouter } from 'next/navigation'
import React from 'react'

const MentorProfileButton = ({ id }: { id: number }) => {
  const router = useRouter()

  const handleMentorProfile = () => {
    if (isWebView()) {
      sendMessageToNative({
        type: WebViewMessageTypes.MENTOR_PROFILE,
        data: { mentorId: id },
      })
    } else {
      router.push(`mentor/${id}/profile`)
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

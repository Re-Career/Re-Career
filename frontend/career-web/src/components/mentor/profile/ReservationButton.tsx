'use client'

import { useRouter } from 'next/navigation'
import React from 'react'

const MentorReservationButton = ({ id }: { id: number }) => {
  const router = useRouter()

  const handleReservation = () => {
    // React Native WebView로 메시지 전송
    if (typeof window !== 'undefined' && (window as any).ReactNativeWebView) {
      return (window as any).ReactNativeWebView.postMessage(
        JSON.stringify({
          type: 'MENTOR_RESERVATION',
          data: { mentorId: id },
        })
      )
    }

    // 일반 웹 브라우저에서는 일반적인 링크로 이동
    router.push(`/mentor/${id}/reservation`)
  }

  return (
    <button
      className="bg-primary w-full rounded-lg py-3 font-bold"
      onClick={handleReservation}
    >
      상담 예약하기
    </button>
  )
}

export default MentorReservationButton

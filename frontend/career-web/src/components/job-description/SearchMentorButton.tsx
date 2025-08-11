'use client'

import { WebViewMessageTypes } from '@/lib/constants/global'
import { sendMessageToNative } from '@/utils/webview'
import { useRouter } from 'next/navigation'
import React from 'react'

const SearchMentorButton = ({ jobId }: { jobId: string }) => {
  const router = useRouter()

  const handleSearchMentor = () => {
    if (window.ReactNativeWebView) {
      sendMessageToNative({
        type: WebViewMessageTypes.SEARCH_MENTOR,
        data: { jobId },
      })
    } else {
      router.push(`/matching?jobId=${jobId}`)
    }
  }

  return (
    <button
      className="bg-primary w-full rounded-xl py-3 font-bold"
      onClick={handleSearchMentor}
    >
      멘토 찾기
    </button>
  )
}

export default SearchMentorButton

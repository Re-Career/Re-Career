'use client'

import { useLoginSheet } from '@/store/useLoginSheet'
import { getCookieValue } from '@/utils/getCookie'

const ReserveButtonWithAuth = ({ id }: { id: string }) => {
  const { onOpen } = useLoginSheet()

  const handleClick = () => {
    const token = getCookieValue('accessToken')

    if (!token) {
      onOpen()
    } else {
      window.location.href = `/mentor/${id}/reservation`
    }
  }

  return (
    <button
      className="bg-primary-500 flex-1 rounded-lg py-3 text-center font-bold"
      onClick={handleClick}
    >
      상담 예약하기
    </button>
  )
}

export default ReserveButtonWithAuth

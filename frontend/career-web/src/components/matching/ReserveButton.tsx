'use client'

import { useLoginSheet } from '@/store/useLoginSheet'
import { getCookieValue } from '@/lib/utils/getCookie'

const ReserveButton = ({ id }: { id: number }) => {
  const { onOpen } = useLoginSheet()
  const handleClick = (e: React.MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()
    const token = getCookieValue('accessToken')

    if (!token) {
      onOpen()
    } else {
      window.location.href = `/mentor/${id}/reservation`
    }
  }

  return (
    <button
      onClick={handleClick}
      className="bg-primary-500 flex-shrink-0 rounded-xl px-4 py-1.5 text-sm"
    >
      1:1 예약
    </button>
  )
}

export default ReserveButton

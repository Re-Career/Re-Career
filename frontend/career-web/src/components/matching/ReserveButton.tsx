'use client'

import { useRouter } from 'next/navigation'

const ReserveButton = ({ id }: { id: number }) => {
  const router = useRouter()

  return (
    <button
      onClick={(e) => {
        e.preventDefault()
        e.stopPropagation()
        router.push(`/mentor/${id}/reservation`)
      }}
      className="bg-primary-500 flex-shrink-0 rounded-xl px-4 py-1.5 text-sm"
    >
      1:1 예약
    </button>
  )
}

export default ReserveButton

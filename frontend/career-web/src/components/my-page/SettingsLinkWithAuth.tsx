'use client'

import { useLoginSheet } from '@/store/useLoginSheet'
import { getCookieValue } from '@/lib/utils/getCookie'
import Link from 'next/link'
import { GoPerson as PersonIcon } from 'react-icons/go'

const SettingsLinkWithAuth = () => {
  const { onOpen } = useLoginSheet()

  const handleClick = (e: React.MouseEvent) => {
    const token = getCookieValue('accessToken')

    if (!token) {
      e.preventDefault()
      onOpen()
    } else {
      window.location.href = '/settings/profile'
    }
  }

  return (
    <Link
      href="/settings/profile"
      className="flex items-center gap-4"
      onClick={handleClick}
    >
      <div className="w-min rounded-lg bg-gray-100 p-3">
        <PersonIcon className="h-6 w-6" />
      </div>
      <p>계정 수정</p>
    </Link>
  )
}

export default SettingsLinkWithAuth

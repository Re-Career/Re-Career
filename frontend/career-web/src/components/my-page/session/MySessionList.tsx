'use client'

import React from 'react'
import { IoCalendarNumberOutline as ScheduledIcon } from 'react-icons/io5'
import { FaCheck } from 'react-icons/fa6'
import Link from 'next/link'
import { SessionStatus } from '@/types/session'
import { useLoginSheet } from '@/store/useLoginSheet'
import { getCookieValue } from '@/utils/getCookie'

const MySessionList = () => {
  const sessionList = [
    {
      key: SessionStatus.CONFIRMED,
      name: '예정됨',
      icon: ScheduledIcon,
      description: '다가오는 상담',
    },
    {
      key: SessionStatus.COMPLETED,
      name: '완료됨',
      icon: FaCheck,
      description: '완료된 상담',
    },
  ]
  const { onOpen } = useLoginSheet()
  const handleClick = (href: string, e: React.MouseEvent) => {
    const token = getCookieValue('accessToken')

    if (!token) {
      e.preventDefault()
      onOpen()
    } else {
      window.location.href = href
    }
  }

  return (
    <section className="space-y-4">
      {sessionList.map(({ key, name, icon: Icon, description }) => (
        <Link
          key={key}
          className="flex gap-4 px-4"
          href={`/sessions/${key}`}
          onClick={(e) => handleClick(`/sessions/${key}`, e)}
        >
          <div className="rounded-lg bg-gray-100 p-3">
            <Icon className="h-6 w-6" />
          </div>
          <div>
            <p>{name}</p>
            <p className="text-sm">{description}</p>
          </div>
        </Link>
      ))}
    </section>
  )
}

export default MySessionList

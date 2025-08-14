import React from 'react'
import { IoCalendarNumberOutline as ScheduledIcon } from 'react-icons/io5'
import { FaCheck } from 'react-icons/fa6'
import Link from 'next/link'

const MyMentoringList = () => {
  const mentoringList = [
    {
      key: 'scheduled',
      name: '예정됨',
      icon: ScheduledIcon,
      description: '다가오는 상담',
    },
    {
      key: 'completed',
      name: '완료됨',
      icon: FaCheck,
      description: '완료된 상담',
    },
  ]

  return (
    <section className="space-y-4">
      {mentoringList.map(({ key, name, icon: Icon, description }) => (
        <Link key={key} className="flex gap-4 px-4" href={`/mentoring/${key}`}>
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

export default MyMentoringList

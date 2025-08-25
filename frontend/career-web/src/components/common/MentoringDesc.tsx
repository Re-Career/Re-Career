import { Session, SessionStatus } from '@/types/session'
import { isToday } from '@/utils/day'
import dayjs from 'dayjs'
import Image from 'next/image'
import Link from 'next/link'
import React from 'react'

const MentoringDesc = ({ session }: { session: Session }) => {
  const { id, sessionTime, status, mentor } = session

  const todaySession = isToday(sessionTime)

  const getStatusBadge = (status: SessionStatus) => {
    switch (status) {
      case SessionStatus.REQUESTED:
        return (
          <span className="inline-flex items-center rounded-full bg-amber-50 px-3 py-1 text-xs font-medium text-amber-700 ring-1 ring-amber-600/20 ring-inset">
            요청됨
          </span>
        )
      case SessionStatus.CONFIRMED:
        return (
          <span className="inline-flex items-center rounded-full bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-700 ring-1 ring-emerald-600/20 ring-inset">
            확정
          </span>
        )
      case SessionStatus.CANCELED:
        return (
          <span className="inline-flex items-center rounded-full bg-red-50 px-3 py-1 text-xs font-medium text-red-700 ring-1 ring-red-600/20 ring-inset">
            취소됨
          </span>
        )
      case SessionStatus.COMPLETED:
        return (
          <span className="inline-flex items-center rounded-full bg-blue-50 px-3 py-1 text-xs font-medium text-blue-700 ring-1 ring-blue-600/20 ring-inset">
            완료
          </span>
        )
      default:
        return null
    }
  }

  return (
    <Link className="group block" href={`/session/${id}`}>
      <div
        className={`rounded-xl p-2 shadow-sm ring-1 transition-all duration-200 hover:shadow-md ${
          todaySession
            ? 'bg-primary-100 ring-primary-500/70 hover:ring-primary-300'
            : 'bg-white ring-gray-200/50 hover:ring-gray-300/50'
        }`}
      >
        <div className="flex items-start gap-4">
          <div className="relative h-16 w-16 flex-shrink-0">
            <Image
              width={64}
              height={64}
              alt="mentor_profile_image"
              src={mentor.profileImageUrl}
              className="h-full w-full rounded-xl object-cover object-top ring-2 ring-gray-100"
            />
          </div>

          <div className="flex-1 space-y-3">
            <div className="flex items-start justify-between">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-gray-900">{mentor.name}</h3>
                  <span className="text-gray-400">•</span>
                  <span className="text-sm text-gray-600">
                    {mentor.position.name}
                  </span>
                </div>
                <p
                  className={`flex items-center gap-1 text-sm ${
                    todaySession ? 'text-blue-600' : 'text-gray-500'
                  }`}
                >
                  {todaySession && (
                    <span className="mr-1 inline-flex items-center rounded-full bg-blue-100 px-2 py-0.5 text-xs font-medium text-blue-800">
                      오늘
                    </span>
                  )}
                  <svg
                    className="h-4 w-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  {dayjs(sessionTime).format('YYYY년 MM월 DD일  hh시 mm분')}
                </p>
              </div>

              <div className="flex-shrink-0">{getStatusBadge(status)}</div>
            </div>
          </div>
        </div>
      </div>
    </Link>
  )
}

export default MentoringDesc

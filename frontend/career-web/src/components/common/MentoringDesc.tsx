import { Session, SessionStatus } from '@/types/session'
import { isToday } from '@/lib/utils/day'
import dayjs from 'dayjs'
import FixedSizeImage from './FixedSizeImage'
import { FaRegClock as ClockIcon } from 'react-icons/fa'
import { useLoginSheet } from '@/store/useLoginSheet'

interface MentoringDescProps {
  session: Session
  priority: boolean
}

const MentoringDesc = ({ session, priority }: MentoringDescProps) => {
  const { id, sessionTime, status, mentor } = session
  const todaySession = isToday(sessionTime)
  const { onOpen } = useLoginSheet()

  const handleClick = (e: React.MouseEvent) => {
    const token =
      typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null

    if (!token) {
      e.preventDefault()
      onOpen()
    } else {
      window.location.href = `/session/${id}`
    }
  }

  const getStatusBadge = (status: SessionStatus) => {
    const baseClasses =
      'inline-flex items-center rounded-full px-3 py-1 text-xs font-medium ring-1 ring-inset'

    const statusConfig = {
      [SessionStatus.REQUESTED]: {
        style: 'bg-amber-50 text-amber-700 ring-amber-600/20',
        text: '요청됨',
      },
      [SessionStatus.CONFIRMED]: {
        style: 'bg-emerald-50 text-emerald-700 ring-emerald-600/20',
        text: '확정',
      },
      [SessionStatus.CANCELED]: {
        style: 'bg-red-50 text-red-700 ring-red-600/20',
        text: '취소됨',
      },
      [SessionStatus.COMPLETED]: {
        style: 'bg-blue-50 text-blue-700 ring-blue-600/20',
        text: '완료',
      },
    }

    const config = statusConfig[status]

    if (!config) return null

    return (
      <span className={`${baseClasses} ${config.style}`}>{config.text}</span>
    )
  }

  return (
    <a className="group block" href={`/session/${id}`} onClick={handleClick}>
      <div
        className={`rounded-xl p-2 shadow-sm ring-1 transition-all duration-200 hover:shadow-md ${
          todaySession
            ? 'bg-primary-100 ring-primary-500/70 hover:ring-primary-300'
            : 'bg-white ring-gray-200/50 hover:ring-gray-300/50'
        }`}
      >
        <div className="flex items-start gap-4">
          <FixedSizeImage
            src={mentor.profileImageUrl}
            alt={`mentor_profile_image_${mentor.name}`}
            divClassName="relative"
            className="ring-2 ring-gray-100"
            size="sm"
            isCircle={false}
            priority={priority}
          />

          <div className="flex-1 space-y-1">
            <div className="flex items-center justify-between">
              <div className="flex flex-1 items-center gap-2">
                <h3 className="font-semibold text-gray-900">{mentor.name}</h3>
                <span className="text-gray-400">•</span>
                <span className="text-sm text-gray-600">
                  {mentor.position.name}
                </span>
              </div>
              <div className="flex-shrink-0">{getStatusBadge(status)}</div>
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
              <ClockIcon />
              {dayjs(sessionTime).format('YYYY년 MM월 DD일  hh시 mm분')}
            </p>
          </div>
        </div>
      </div>
    </a>
  )
}

export default MentoringDesc

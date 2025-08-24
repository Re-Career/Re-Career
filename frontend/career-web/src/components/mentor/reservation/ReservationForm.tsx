'use client'

import { useActionState, useState, useCallback, useMemo } from 'react'
import dayjs from 'dayjs'
import CustomCalendar from './CustomCalendar'
import CustomTimePicker from './CustomTimePicker'
import { DatePiece, DateType } from '@/types/global'
import { handleReserve } from '@/app/actions/reservation/action'
import { isToday } from '@/utils/day'
import useTimeHandler from '@/hooks/useTimeHandler'
import { useEffect } from 'react'
import { useRouter } from 'next/navigation'

const ReservationForm = ({ mentorId }: { mentorId: string }) => {
  const router = useRouter()
  const { minimumTime, isTimeBeforeMinimum } = useTimeHandler()

  const [state, formAction] = useActionState(handleReserve, {
    success: false,
    message: '',
  })

  useEffect(() => {
    if (state.success) {
      const { data } = state

      if (data) {
        router.replace(`/mentor/reservation/success/${data.id}`)
      }
    }
  }, [state])

  const [date, setDate] = useState<DatePiece>(new Date())
  const [timeValue, setTimeValue] = useState(minimumTime)

  const handleDate = useCallback(
    (date: DateType) => {
      if (!date || Array.isArray(date)) return

      setDate(date)

      // 오늘 날짜로 변경 시 시간 검증 및 재설정
      if (isToday(date) && isTimeBeforeMinimum(timeValue)) {
        setTimeValue(minimumTime)
      }
    },
    [timeValue]
  )

  return (
    <form action={formAction}>
      <input name="mentorId" value={mentorId} type="hidden" />
      <input
        name="sessionTime"
        value={date ? dayjs(date).format() : ''}
        type="hidden"
      />

      <h4 className="section-title">날짜 및 시간 선택</h4>
      <CustomCalendar value={date} onChange={handleDate} />
      <CustomTimePicker
        key={useMemo(
          () =>
            `${dayjs(date).format('YYYY-MM-DD')}-${timeValue.hour}-${timeValue.minute}-${timeValue.period}`,
          [date, timeValue]
        )}
        value={timeValue}
        onChange={setTimeValue}
        selectedDate={date}
      />

      <div className="sticky bottom-0 z-60 flex border-t border-gray-100 bg-white p-4">
        <button
          className="bg-primary-500 flex-1 rounded-lg py-3 text-center font-bold"
          type="submit"
        >
          상담 예약하기
        </button>
      </div>
    </form>
  )
}

export default ReservationForm

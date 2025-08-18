'use client'
import React, { useActionState, useState } from 'react'
import dayjs from 'dayjs'
import CustomCalendar from './CustomCalendar'
import CustomTimePicker from './CustomTimePicker'
import { DatePiece, DateType } from '@/types/global'
import { handleReserve } from '@/app/actions/reservation/action'

const ReservationForm = ({ mentorId }: { mentorId: string }) => {
  const [, formAction] = useActionState(handleReserve, {
    success: false,
    message: '',
  })

  const [date, setDate] = useState<DatePiece>(new Date())
  const [timeValue, setTimeValue] = useState({
    hour: 10,
    minute: 30,
    period: 'AM',
  })

  const handleDate = (date: DateType) => {
    if (date && !Array.isArray(date)) {
      setDate(date)
    }
  }

  return (
    <form action={formAction}>
      <input
        name="mentorId"
        value={mentorId}
        className="hidden"
        onChange={() => {}}
      />
      <input
        name="dateTime"
        value={
          date
            ? `${dayjs(date).format('YYYY-MM-DD')} ${timeValue.hour.toString().padStart(2, '0')}:${timeValue.minute.toString().padStart(2, '0')}`
            : ''
        }
        className="hidden"
        onChange={() => {}}
      />
      <section className="px-4 py-4">
        <h4 className="text-lg font-bold">날짜 및 시간 선택</h4>
        <CustomCalendar value={date} onChange={handleDate} />
        <CustomTimePicker value={timeValue} onChange={setTimeValue} />
      </section>
      <section className="px-4 py-3">
        <h4 className="py-4 text-lg font-bold">알림</h4>
        <label className="flex justify-between">
          <span>이메일 알림</span>
          <input name="emailAlert" type="checkbox" className="h-5 w-5" />
        </label>
      </section>
      <section className="px-4 py-3">
        <button className="bg-primary-500 h-12 w-full rounded-2xl text-lg font-bold">
          상담 예약하기
        </button>
      </section>
    </form>
  )
}

export default ReservationForm

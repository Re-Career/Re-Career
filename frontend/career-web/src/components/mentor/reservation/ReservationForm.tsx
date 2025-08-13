'use client'
import React, { useState } from 'react'
import CustomCalendar from './CustomCalendar'
import CustomTimePicker from './CustomTimePicker'
import { DateType } from '@/types/global'

const ReservationForm = () => {
  const [date, setDate] = useState<DateType>(new Date())
  const [timeValue, setTimeValue] = useState({
    hour: 10,
    minute: 30,
    period: 'AM',
  })

  const handleDate = (date: DateType) => {
    setDate(date)
  }

  return (
    <form>
      <section className="px-4 py-4">
        <h4 className="text-lg font-bold">날짜 및 시간 선택</h4>
        <CustomCalendar value={date} onChange={handleDate} />
        <CustomTimePicker value={timeValue} onChange={setTimeValue} />
      </section>
      <section className="px-4 py-3">
        <h4 className="py-4 text-lg font-bold">알림</h4>
        <label className="flex justify-between">
          <span>이메일 알림</span> <input type="checkbox" className="h-5 w-5" />
        </label>
      </section>
      <section className="px-4 py-3">
        <button className="bg-primary h-12 w-full rounded-2xl text-lg font-bold">
          상담 예약하기
        </button>
      </section>
    </form>
  )
}

export default ReservationForm

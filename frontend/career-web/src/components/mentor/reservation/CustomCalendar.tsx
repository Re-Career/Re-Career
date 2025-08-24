'use client'

import React from 'react'
import Calendar from 'react-calendar'
import dayjs from 'dayjs'
import { DatePiece, DateType } from '@/types/global'

interface CustomCalendarProps {
  value: DatePiece
  onChange: (date: DateType) => void
}

const CustomCalendar = ({ value, onChange }: CustomCalendarProps) => {
  const today = dayjs().startOf('day').toDate()
  const currentMonth = dayjs().startOf('month').toDate()
  const [activeDate, setActiveDate] = React.useState(new Date())

  return (
    <Calendar
      value={value}
      onChange={onChange}
      onActiveStartDateChange={({ activeStartDate }) => {
        if (activeStartDate) {
          setActiveDate(activeStartDate)
        }
      }}
      formatMonthYear={(_, date) => dayjs(date).format('YYYY년 M월')}
      formatDay={(_, date) => dayjs(date).format('D')}
      prevLabel={dayjs(activeDate).isSame(dayjs(), 'month') ? '' : '‹'}
      nextLabel="›"
      prev2Label={null}
      next2Label={null}
      minDate={currentMonth}
      tileDisabled={({ date, view }) => {
        return view === 'month' && date < today
      }}
    />
  )
}

export default CustomCalendar

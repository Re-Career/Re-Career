'use client'

import React from 'react'
import Calendar from 'react-calendar'
import { DatePiece, DateType } from '@/types/global'
import {
  getCurrentMonth,
  formatMonthYear,
  formatDay,
  isBeforeToday,
  isSameMonth,
} from '@/lib/utils/day'

interface CustomCalendarProps {
  value: DatePiece
  onChange: (date: DateType) => void
}

const CustomCalendar = ({ value, onChange }: CustomCalendarProps) => {
  const currentMonth = getCurrentMonth()
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
      formatMonthYear={(_, date) => formatMonthYear(date)}
      formatDay={(_, date) => formatDay(date)}
      prevLabel={isSameMonth(activeDate, new Date()) ? '' : '‹'}
      nextLabel="›"
      prev2Label={null}
      next2Label={null}
      minDate={currentMonth}
      tileDisabled={({ date, view }) => {
        return view === 'month' && isBeforeToday(date)
      }}
    />
  )
}

export default CustomCalendar

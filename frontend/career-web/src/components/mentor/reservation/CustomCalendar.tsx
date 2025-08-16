'use client'

import React from 'react'
import Calendar from 'react-calendar'
import { ko } from 'date-fns/locale'
import { format } from 'date-fns'
import { DatePiece, DateType } from '@/types/global'

interface CustomCalendarProps {
  value: DatePiece
  onChange: (date: DateType) => void
}

const CustomCalendar = ({ value, onChange }: CustomCalendarProps) => {
  return (
    <Calendar
      value={value}
      onChange={onChange}
      formatMonthYear={(locale, date) =>
        format(date, 'yyyy년 M월', { locale: ko })
      }
      formatDay={(locale: string | undefined, date: Date) => {
        return format(date, 'd')
      }}
      prevLabel="‹"
      nextLabel="›"
      prev2Label={null}
      next2Label={null}
    />
  )
}

export default CustomCalendar

'use client'

import React, { useState } from 'react'
import Calendar from 'react-calendar'
import { ko } from 'date-fns/locale'
import { format } from 'date-fns'
import { DateType } from '@/types/global'

interface CustomCalendarProps {
  value: DateType
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

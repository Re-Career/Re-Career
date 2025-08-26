import { DatePiece } from '@/types/global'
import dayjs from 'dayjs'

// 달력 관련 유틸리티 함수들

// 오늘 날짜 (시작 시간으로 설정)
export const getToday = () => dayjs().startOf('day').toDate()

// 현재 월의 첫 날
export const getCurrentMonth = () => dayjs().startOf('month').toDate()

// 날짜 포맷팅
export const formatMonthYear = (date: Date) => dayjs(date).format('YYYY년 M월')

export const formatDay = (date: Date) => dayjs(date).format('D')

// 날짜가 오늘 이전인지 확인
export const isBeforeToday = (date: Date) => {
  const today = getToday()

  return date < today
}

// 두 날짜가 같은 월인지 확인
export const isSameMonth = (date1: Date, date2: Date) => {
  return dayjs(date1).isSame(dayjs(date2), 'month')
}

// 시간 변환 유틸리티
export const convertTo12HourFormat = (hour24: number) => ({
  hour: hour24 === 0 ? 12 : hour24 > 12 ? hour24 - 12 : hour24,
  period: hour24 >= 12 ? 'PM' : 'AM',
})

export const convertTo24HourFormat = (hour12: number, period: string) => {
  if (period === 'AM') {
    return hour12 === 12 ? 0 : hour12
  }

  return hour12 === 12 ? 12 : hour12 + 12
}

// 특정 날짜가 오늘인지 확인
export const isToday = (date: DatePiece): boolean => {
  return dayjs(date).isSame(dayjs(), 'day')
}

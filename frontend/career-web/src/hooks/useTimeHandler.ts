import { convertTo12HourFormat, convertTo24HourFormat } from '@/utils/day'
import dayjs from 'dayjs'
import { useCallback, useMemo } from 'react'

export interface TimeValue {
  hour: number
  minute: number
  period: string
}

// 상수 정의
const MINUTE_STEP = 30
const HOURS_ARRAY = Array.from({ length: 12 }, (_, i) => i + 1)
const MINUTES_ARRAY = [0, 30]
const PERIODS_ARRAY = ['AM', 'PM']

const useTimeHandler = () => {
  // 현재 시간 + 30분 후의 최소 예약 가능 시간 반환 (메모이제이션)
  const minimumTime = useMemo(() => {
    const now = dayjs().add(MINUTE_STEP, 'minute')
    const { hour, period } = convertTo12HourFormat(now.hour())

    return {
      hour,
      minute: now.minute() >= MINUTE_STEP ? MINUTE_STEP : 0,
      period,
    }
  }, [])

  // 오늘 날짜에 대한 사용 가능한 시간 필터링 (함수 메모이제이션)
  const getAvailableTimesForToday = useCallback(
    (selectedHour: number, selectedPeriod: string) => {
      const now = dayjs().add(MINUTE_STEP, 'minute')
      const currentHour = now.hour()
      const currentMinute = now.minute()
      const minMinute = currentMinute >= MINUTE_STEP ? MINUTE_STEP : 0

      const getDisplayHour = (hour: number) => (hour === 0 ? 12 : hour)

      // AM/PM에 따른 필터링 로직
      const filterLogic = {
        AM: () => {
          const displayHour = getDisplayHour(currentHour)
          const filteredHours = HOURS_ARRAY.filter((h) => h >= displayHour)
          const filteredMinutes =
            selectedHour === displayHour
              ? MINUTES_ARRAY.filter((m) => m >= minMinute)
              : MINUTES_ARRAY

          return {
            availableHours: filteredHours,
            availableMinutes: filteredMinutes,
            availablePeriods: PERIODS_ARRAY,
          }
        },
        PM: () => {
          const displayHour = currentHour === 12 ? 12 : currentHour - 12
          const filteredHours = HOURS_ARRAY.filter((h) => h >= displayHour)
          const filteredMinutes =
            selectedHour === displayHour && selectedPeriod === 'PM'
              ? MINUTES_ARRAY.filter((m) => m >= minMinute)
              : MINUTES_ARRAY

          return {
            availableHours: filteredHours,
            availableMinutes: filteredMinutes,
            availablePeriods: ['PM'],
          }
        },
      }

      return currentHour < 12 ? filterLogic.AM() : filterLogic.PM()
    },
    []
  )

  // 선택된 시간이 최소 시간보다 이전인지 확인 (함수 메모이제이션)
  const isTimeBeforeMinimum = useCallback((timeValue: TimeValue): boolean => {
    const minTime = dayjs().add(MINUTE_STEP, 'minute')
    const selectedHour24 = convertTo24HourFormat(
      timeValue.hour,
      timeValue.period
    )
    const selectedTime = dayjs().hour(selectedHour24).minute(timeValue.minute)

    return selectedTime.isBefore(minTime)
  }, [])

  return {
    getAvailableTimesForToday,
    minimumTime,
    isTimeBeforeMinimum,
  }
}

export default useTimeHandler

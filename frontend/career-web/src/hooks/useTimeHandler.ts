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

    // 분이 30분 이상이면 다음 시간의 0분으로, 30분 미만이면 현재 시간의 30분으로
    let finalMinute = 0
    let finalHour = hour
    let finalPeriod = period

    if (now.minute() > 30) {
      const nextHour = now.add(1, 'hour')
      const converted = convertTo12HourFormat(nextHour.hour())

      // 다음 시간의 0분
      finalMinute = 0
      finalHour = converted.hour
      finalPeriod = converted.period
    } else if (now.minute() > 0) {
      // 현재 시간의 30분
      finalMinute = 30
    }

    return {
      hour: finalHour,
      minute: finalMinute,
      period: finalPeriod,
    }
  }, [])

  // 오늘 날짜에 대한 사용 가능한 시간 필터링 (함수 메모이제이션)
  const getAvailableTimesForToday = useCallback(
    (selectedHour: number, selectedPeriod: string) => {
      // minimumTime을 기준으로 필터링
      const minHour = minimumTime.hour
      const minMinute = minimumTime.minute
      const minPeriod = minimumTime.period

      let availableHours = HOURS_ARRAY
      let availableMinutes = MINUTES_ARRAY
      let availablePeriods = PERIODS_ARRAY

      // AM 시간대 필터링
      if (minPeriod === 'AM') {
        // AM에서만 시작 가능한 경우
        availableHours = HOURS_ARRAY.filter((h) => h >= minHour)
        availablePeriods = PERIODS_ARRAY

        // 선택된 시간이 최소 시간과 같은 경우 분 필터링
        if (selectedHour === minHour && selectedPeriod === 'AM') {
          availableMinutes = MINUTES_ARRAY.filter((m) => m >= minMinute)
        }
      } else {
        // PM에서 시작하는 경우
        if (selectedPeriod === 'AM') {
          // AM 선택 시 모든 시간 불가능
          availableHours = []
          availableMinutes = []
        } else {
          // PM 선택 시 최소 시간 이후만 가능
          availableHours = HOURS_ARRAY.filter((h) => h >= minHour)
          if (selectedHour === minHour) {
            availableMinutes = MINUTES_ARRAY.filter((m) => m >= minMinute)
          }
        }
        availablePeriods = ['PM']
      }

      return {
        availableHours,
        availableMinutes,
        availablePeriods,
      }
    },
    [minimumTime]
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

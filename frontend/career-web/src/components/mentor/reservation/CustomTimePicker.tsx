'use client'

import { useRef, useMemo } from 'react'
import { DatePiece } from '@/types/global'

import { calculateScrollPosition } from '@/utils/scroll'
import { useScrollHandler } from '@/hooks/useScrollHandler'
import useTimeHandler, { TimeValue } from '@/hooks/useTimeHandler'
import { isToday } from '@/utils/day'

export const ITEM_HEIGHT = 44
export const SCROLL_DEBOUNCE_DELAY = 100

interface CustomTimePickerProps {
  value: TimeValue
  onChange: (value: TimeValue) => void
  selectedDate?: DatePiece
}

const CustomTimePicker = ({
  value,
  onChange,
  selectedDate,
}: CustomTimePickerProps) => {
  const {
    hour: selectedHour,
    minute: selectedMinute,
    period: selectedPeriod,
  } = value

  const { getAvailableTimesForToday } = useTimeHandler()

  const { handleScroll, scrollTimeoutRef } = useScrollHandler({
    itemHeight: ITEM_HEIGHT,
    debounceDelayTime: SCROLL_DEBOUNCE_DELAY,
  })

  const hourRef = useRef<HTMLDivElement>(null)
  const minuteRef = useRef<HTMLDivElement>(null)
  const periodRef = useRef<HTMLDivElement>(null)

  const hours = Array.from({ length: 12 }, (_, i) => i + 1)
  const minutes = [0, 30]
  const periods = ['AM', 'PM']

  // 선택된 날짜가 오늘인지 확인하고 현재 시간 이후만 허용
  const { availableHours, availableMinutes, availablePeriods } = useMemo(() => {
    if (!selectedDate || !isToday(selectedDate)) {
      return {
        availableHours: hours,
        availableMinutes: minutes,
        availablePeriods: periods,
      }
    }

    return getAvailableTimesForToday(selectedHour, selectedPeriod)
  }, [selectedDate, selectedHour, selectedPeriod, getAvailableTimesForToday])

  // Period 전용 스크롤 핸들러
  const handlePeriodScroll = () => {
    if (!periodRef.current) return

    if (scrollTimeoutRef.current) {
      clearTimeout(scrollTimeoutRef.current)
    }

    scrollTimeoutRef.current = setTimeout(() => {
      if (!periodRef.current) return
      const scrollTop = periodRef.current.scrollTop

      onChange({ ...value, period: scrollTop < 22 ? 'AM' : 'PM' })
    }, 100)
  }

  // 공통 아이템 클릭 핸들러
  const handleItemClick = <T extends string | number>(
    key: keyof TimeValue,
    item: T,
    ref: React.RefObject<HTMLDivElement | null>,
    items: T[]
  ) => {
    onChange({ ...value, [key]: item })
    const index = items.indexOf(item)

    ref.current?.scrollTo({
      top: index * ITEM_HEIGHT,
      behavior: 'smooth',
    })
  }

  // 초기 스크롤 위치 계산
  const initialHourScroll = calculateScrollPosition({
    items: availableHours,
    itemHeight: ITEM_HEIGHT,
    selectedItem: selectedHour,
  })
  const initialMinuteScroll = calculateScrollPosition({
    items: availableMinutes,
    itemHeight: ITEM_HEIGHT,
    selectedItem: selectedMinute,
  })
  const initialPeriodScroll = calculateScrollPosition({
    items: availablePeriods,
    itemHeight: ITEM_HEIGHT,
    selectedItem: selectedPeriod,
  })

  // Ref 콜백으로 초기 스크롤 설정
  const setHourRef = (element: HTMLDivElement | null) => {
    hourRef.current = element
    if (element) element.scrollTop = initialHourScroll
  }

  const setMinuteRef = (element: HTMLDivElement | null) => {
    minuteRef.current = element
    if (element) element.scrollTop = initialMinuteScroll
  }

  const setPeriodRef = (element: HTMLDivElement | null) => {
    periodRef.current = element
    if (element) element.scrollTop = initialPeriodScroll
  }

  return (
    <div className="relative mx-auto flex max-w-[300px]">
      {/* 선택 표시줄 */}
      <div className="pointer-events-none absolute inset-0 z-0 flex items-center">
        <div className="h-11 w-full rounded-xl bg-gray-100"></div>
      </div>

      {/* Hour Column */}
      <div className="flex-1">
        <div
          ref={setHourRef}
          className="scrollbar-hide h-[132px] snap-y snap-mandatory overflow-y-scroll"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
          onScroll={() =>
            handleScroll({
              ref: hourRef,
              items: availableHours,
              setter: (hour) => onChange({ ...value, hour }),
            })
          }
        >
          <div className="h-11"></div>
          {availableHours.map((hour) => (
            <div
              key={hour}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                hour === selectedHour
                  ? 'font-semibold text-black'
                  : 'text-gray-400'
              }`}
              onClick={() =>
                handleItemClick('hour', hour, hourRef, availableHours)
              }
            >
              {hour.toString().padStart(2, '0')}
            </div>
          ))}
          <div className="h-11"></div>
        </div>
      </div>

      {/* Minute Column */}
      <div className="flex-1">
        <div
          ref={setMinuteRef}
          className="scrollbar-hide h-[132px] snap-y snap-mandatory overflow-y-scroll"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
          onScroll={() =>
            handleScroll({
              ref: minuteRef,
              items: availableMinutes,
              setter: (minute) => onChange({ ...value, minute }),
            })
          }
        >
          <div className="h-11"></div>
          {availableMinutes.map((minute) => (
            <div
              key={minute}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                minute === selectedMinute
                  ? 'font-semibold text-black'
                  : 'text-gray-400'
              }`}
              onClick={() =>
                handleItemClick('minute', minute, minuteRef, availableMinutes)
              }
            >
              {minute.toString().padStart(2, '0')}
            </div>
          ))}
          <div className="h-11"></div>
        </div>
      </div>

      {/* Period Column */}
      <div className="flex-1">
        <div
          ref={setPeriodRef}
          className="scrollbar-hide h-[132px] snap-y snap-mandatory overflow-y-scroll"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
          onScroll={handlePeriodScroll}
        >
          <div className="h-11"></div>
          {availablePeriods.map((period) => (
            <div
              key={period}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                period === selectedPeriod
                  ? 'font-semibold text-black'
                  : 'text-gray-400'
              }`}
              onClick={() =>
                handleItemClick('period', period, periodRef, availablePeriods)
              }
            >
              {period}
            </div>
          ))}
          <div className="h-11"></div>
        </div>
      </div>
    </div>
  )
}

export default CustomTimePicker

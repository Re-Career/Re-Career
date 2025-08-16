'use client'

import { useRef, useCallback } from 'react'

interface TimeValue {
  hour: number
  minute: number
  period: string
}

interface CustomTimePickerProps {
  value: TimeValue
  onChange: (value: TimeValue) => void
}

const CustomTimePicker = ({ value, onChange }: CustomTimePickerProps) => {
  const { hour: selectedHour, minute: selectedMinute, period: selectedPeriod } = value
  const hourRef = useRef<HTMLDivElement>(null)
  const minuteRef = useRef<HTMLDivElement>(null)
  const periodRef = useRef<HTMLDivElement>(null)
  const scrollTimeoutRef = useRef<NodeJS.Timeout | null>(null)

  const hours = Array.from({ length: 12 }, (_, i) => i + 1)
  const minutes = [0, 30]
  const periods = ['AM', 'PM']

  // 디바운싱된 스크롤 핸들러
  const handleScroll = useCallback((
    ref: React.RefObject<HTMLDivElement | null>,
    items: any[],
    setter: (value: any) => void
  ) => {
    if (!ref.current) return

    if (scrollTimeoutRef.current) {
      clearTimeout(scrollTimeoutRef.current)
    }

    scrollTimeoutRef.current = setTimeout(() => {
      if (!ref.current) return
      const scrollTop = ref.current.scrollTop
      const itemHeight = 44
      const index = Math.round(scrollTop / itemHeight)
      const clampedIndex = Math.max(0, Math.min(index, items.length - 1))

      setter(items[clampedIndex])
    }, 100)
  }, [])

  // Period 전용 스크롤 핸들러
  const handlePeriodScroll = useCallback(() => {
    if (!periodRef.current) return

    if (scrollTimeoutRef.current) {
      clearTimeout(scrollTimeoutRef.current)
    }

    scrollTimeoutRef.current = setTimeout(() => {
      if (!periodRef.current) return
      const scrollTop = periodRef.current.scrollTop

      onChange({ ...value, period: scrollTop < 22 ? 'AM' : 'PM' })
    }, 100)
  }, [value, onChange])

  // 초기 스크롤 위치 계산
  const initialHourScroll = hours.indexOf(selectedHour) * 44
  const initialMinuteScroll = minutes.indexOf(selectedMinute) * 44
  const initialPeriodScroll = periods.indexOf(selectedPeriod) * 44

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
          onScroll={() => handleScroll(hourRef, hours, (hour) => onChange({ ...value, hour }))}
        >
          <div className="h-11"></div>
          {hours.map((hour) => (
            <div
              key={hour}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                hour === selectedHour ? 'font-semibold text-black' : 'text-gray-400'
              }`}
              onClick={() => {
                onChange({ ...value, hour })
                const index = hours.indexOf(hour)

                hourRef.current?.scrollTo({ top: index * 44, behavior: 'smooth' })
              }}
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
          onScroll={() => handleScroll(minuteRef, minutes, (minute) => onChange({ ...value, minute }))}
        >
          <div className="h-11"></div>
          {minutes.map((minute) => (
            <div
              key={minute}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                minute === selectedMinute ? 'font-semibold text-black' : 'text-gray-400'
              }`}
              onClick={() => {
                onChange({ ...value, minute })
                const index = minutes.indexOf(minute)

                minuteRef.current?.scrollTo({ top: index * 44, behavior: 'smooth' })
              }}
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
          {periods.map((period) => (
            <div
              key={period}
              className={`relative z-10 flex h-11 cursor-pointer snap-center items-center justify-center text-sm ${
                period === selectedPeriod ? 'font-semibold text-black' : 'text-gray-400'
              }`}
              onClick={() => {
                onChange({ ...value, period })
                const index = periods.indexOf(period)

                periodRef.current?.scrollTo({ top: index * 44, behavior: 'smooth' })
              }}
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
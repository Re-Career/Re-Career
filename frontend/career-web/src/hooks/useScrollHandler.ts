import { useCallback, useRef, useMemo } from 'react'

// 스크롤 핸들러 타입 정의
type ScrollHandlerParams<T> = {
  ref: React.RefObject<HTMLDivElement | null>
  items: T[]
  setter: (value: T) => void
}

type UseScrollHandlerConfig = {
  itemHeight: number
  debounceDelayTime: number
}

export const useScrollHandler = ({
  itemHeight,
  debounceDelayTime,
}: UseScrollHandlerConfig) => {
  const scrollTimeoutRef = useRef<NodeJS.Timeout | null>(null)

  // 스크롤 인덱스 계산 로직 분리
  const calculateScrollIndex = useMemo(() => {
    return (scrollTop: number, itemsLength: number): number => {
      const index = Math.round(scrollTop / itemHeight)

      return Math.max(0, Math.min(index, itemsLength - 1))
    }
  }, [itemHeight])

  // 디바운스 타이머 정리 로직
  const clearScrollTimeout = useCallback(() => {
    if (scrollTimeoutRef.current) {
      clearTimeout(scrollTimeoutRef.current)
      scrollTimeoutRef.current = null
    }
  }, [])

  const handleScroll = useCallback(
    <T>({ ref, items, setter }: ScrollHandlerParams<T>) => {
      if (!ref.current) return

      clearScrollTimeout()

      scrollTimeoutRef.current = setTimeout(() => {
        if (!ref.current) return

        const scrollTop = ref.current.scrollTop
        const selectedIndex = calculateScrollIndex(scrollTop, items.length)

        setter(items[selectedIndex])
      }, debounceDelayTime)
    },
    [clearScrollTimeout, calculateScrollIndex, debounceDelayTime]
  )

  return {
    handleScroll,
    scrollTimeoutRef,
    clearScrollTimeout,
  }
}

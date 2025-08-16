'use client'

import { useHorizontalScroll } from '@/hooks/useHorizontalScroll'
import { ReactNode } from 'react'

interface HorizontalScrollProps {
  children: ReactNode
}

const HorizontalScroll = ({ children }: HorizontalScrollProps) => {
  const scrollRef = useHorizontalScroll()

  return (
    <div ref={scrollRef} className="scrollbar-hide cursor-grab overflow-x-auto">
      <div className="flex gap-3 px-4">
        {children}
        <div className="w-1 flex-shrink-0"></div>
      </div>
    </div>
  )
}

export default HorizontalScroll

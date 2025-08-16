import { useRef, useEffect, useCallback } from 'react'

export const useHorizontalScroll = () => {
  const scrollRef = useRef<HTMLDivElement>(null)
  const isDown = useRef(false)
  const startX = useRef(0)
  const scrollLeft = useRef(0)
  const hasMoved = useRef(false)

  const handleMouseDown = useCallback((e: MouseEvent) => {
    const slider = scrollRef.current

    if (!slider) return

    isDown.current = true
    hasMoved.current = false
    slider.style.cursor = 'grabbing'
    startX.current = e.pageX - slider.offsetLeft
    scrollLeft.current = slider.scrollLeft
    e.preventDefault()
  }, [])

  const handleMouseLeave = useCallback(() => {
    const slider = scrollRef.current

    if (!slider) return

    isDown.current = false
    slider.style.cursor = 'grab'
  }, [])

  const handleMouseUp = useCallback(() => {
    const slider = scrollRef.current

    if (!slider) return

    isDown.current = false
    slider.style.cursor = 'grab'
  }, [])

  const handleMouseMove = useCallback((e: MouseEvent) => {
    const slider = scrollRef.current

    if (!slider || !isDown.current) return

    e.preventDefault()
    const x = e.pageX - slider.offsetLeft
    const walk = (x - startX.current) * 1.5

    if (Math.abs(walk) > 3) {
      hasMoved.current = true
    }

    slider.scrollLeft = scrollLeft.current - walk
  }, [])

  useEffect(() => {
    const slider = scrollRef.current

    if (!slider) return

    const handleClick = (e: MouseEvent) => {
      if (hasMoved.current) {
        e.preventDefault()
        e.stopPropagation()
        hasMoved.current = false
      }
    }

    slider.addEventListener('mousedown', handleMouseDown)
    slider.addEventListener('mouseleave', handleMouseLeave)
    slider.addEventListener('mouseup', handleMouseUp)
    slider.addEventListener('mousemove', handleMouseMove)
    slider.addEventListener('click', handleClick, true)

    const handleGlobalMouseUp = () => {
      if (isDown.current && slider) {
        isDown.current = false
        slider.style.cursor = 'grab'
      }
    }

    document.addEventListener('mouseup', handleGlobalMouseUp)

    return () => {
      slider.removeEventListener('mousedown', handleMouseDown)
      slider.removeEventListener('mouseleave', handleMouseLeave)
      slider.removeEventListener('mouseup', handleMouseUp)
      slider.removeEventListener('mousemove', handleMouseMove)
      slider.removeEventListener('click', handleClick, true)
      document.removeEventListener('mouseup', handleGlobalMouseUp)
    }
  }, [handleMouseDown, handleMouseLeave, handleMouseUp, handleMouseMove])

  return scrollRef
}

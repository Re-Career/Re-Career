'use client'

import { Fragment, useEffect, useState, ReactNode } from 'react'

type MenuItem = {
  label: string
  onClick: () => void
  isDestructive?: boolean
}

type BottomSheetProps = {
  isOpen: boolean
  onClose: () => void
  children?: ReactNode
  menuItems?: MenuItem[]
}

const BottomSheet = ({
  isOpen,
  onClose,
  children,
  menuItems,
}: BottomSheetProps) => {
  const [isVisible, setIsVisible] = useState(false)
  const [isAnimating, setIsAnimating] = useState(false)

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose()
      }
    }

    if (isOpen) {
      setIsVisible(true)
      setTimeout(() => setIsAnimating(true), 10)
      document.addEventListener('keydown', handleKeyDown)
      document.documentElement.style.overflow = 'hidden'
    } else {
      setIsAnimating(false)
      const timer = setTimeout(() => {
        setIsVisible(false)
        document.documentElement.style.overflow = 'unset'
      }, 300)

      return () => clearTimeout(timer)
    }

    return () => {
      document.removeEventListener('keydown', handleKeyDown)
      document.documentElement.style.overflow = 'unset'
    }
  }, [isOpen, onClose])

  if (!isVisible) return null

  return (
    <div className="fixed inset-0 z-70 flex items-end">
      <div
        className={`absolute inset-0 bg-black/40 transition-opacity duration-300 ease-out ${
          isAnimating ? 'opacity-100' : 'opacity-0'
        }`}
        onClick={onClose}
        aria-hidden="true"
      />
      <div
        className={`relative mx-auto flex max-h-[90vh] w-full max-w-[450px] transform flex-col rounded-t-3xl bg-white shadow-xl transition-transform duration-300 ease-out ${
          isAnimating ? 'translate-y-0' : 'translate-y-full'
        }`}
        role="dialog"
        aria-modal="true"
        aria-labelledby="bottom-sheet-title"
      >
        {children ? (
          // children이 있으면 바로 렌더링
          children
        ) : (
          // menuItems가 있으면 리스트 형태로 렌더링
          <>
            <div className="flex justify-center pt-3 pb-2">
              <div className="h-1 w-12 rounded-full bg-gray-300" />
            </div>

            <div className="flex-1 overflow-y-auto px-4">
              <div className="space-y-1">
                {menuItems?.map((item, index) => (
                  <Fragment key={index}>
                    <button
                      onClick={() => {
                        item.onClick()
                        onClose()
                      }}
                      className={`w-full rounded-lg px-4 py-4 text-left text-lg transition-colors duration-150 hover:bg-gray-50 active:bg-gray-100 ${
                        item.isDestructive
                          ? 'text-red-600 hover:bg-red-50 active:bg-red-100'
                          : 'text-gray-900'
                      }`}
                    >
                      {item.label}
                    </button>
                    {index < (menuItems?.length || 0) - 1 && (
                      <div className="mx-4 h-px bg-gray-200" />
                    )}
                  </Fragment>
                ))}
              </div>
            </div>

            <div className="border-t border-gray-100 bg-white p-4">
              <button
                onClick={onClose}
                className="w-full rounded-lg px-4 py-4 text-lg text-gray-600 transition-colors duration-150 hover:bg-gray-50 active:bg-gray-100"
              >
                취소
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default BottomSheet

'use client'

import { Fragment, useEffect } from 'react'

type MenuItem = {
  label: string
  onClick: () => void
  isDestructive?: boolean
}

type BottomSheetProps = {
  isOpen: boolean
  onClose: () => void
  menuItems: MenuItem[]
}

const BottomSheet = ({ isOpen, onClose, menuItems }: BottomSheetProps) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose()
      }
    }

    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown)
      document.body.style.overflow = 'hidden'
    }

    return () => {
      document.removeEventListener('keydown', handleKeyDown)
      document.body.style.overflow = 'unset'
    }
  }, [isOpen, onClose])

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-70 flex items-end">
      <div
        className="absolute inset-0 bg-black/40 transition-opacity duration-300 ease-out"
        onClick={onClose}
        aria-hidden="true"
      />
      <div
        className="relative mx-auto w-full max-w-[450px] translate-y-0 transform rounded-t-3xl bg-white shadow-xl transition-transform duration-300 ease-out"
        role="dialog"
        aria-modal="true"
        aria-labelledby="bottom-sheet-title"
      >
        <div className="flex justify-center pt-3 pb-2">
          <div className="h-1 w-12 rounded-full bg-gray-300" />
        </div>

        <div className="px-4 pb-8">
          <div className="space-y-1">
            {menuItems.map((item, index) => (
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
                {index < menuItems.length - 1 && (
                  <div className="mx-4 h-px bg-gray-200" />
                )}
              </Fragment>
            ))}
          </div>

          <div className="mt-6 border-t border-gray-100 pt-4">
            <button
              onClick={onClose}
              className="w-full rounded-lg px-4 py-4 text-lg text-gray-600 transition-colors duration-150 hover:bg-gray-50 active:bg-gray-100"
            >
              취소
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default BottomSheet

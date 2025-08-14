'use client'

import React from 'react'
import { sendMessageToNative } from '../../utils/webview'
import { useRouter } from 'next/navigation'
import { WebViewMessageTypes } from '@/lib/constants/global'
import { IoArrowBack } from 'react-icons/io5'

interface HeaderProps {
  title?: string
  showBackButton?: boolean
  showCancelButton?: boolean
  onBackPress?: () => void
  rightElement?: React.ReactNode
}

const Header: React.FC<HeaderProps> = ({
  title,
  showBackButton = false,
  showCancelButton = false,
  onBackPress,
  rightElement,
}) => {
  const router = useRouter()
  const onCancelPress = () => {
    sendMessageToNative({ type: WebViewMessageTypes.CLOSE_WEBVIEW })
  }

  const handleBackPress = () => {
    router.back()

    onBackPress && onBackPress()
  }

  return (
    <header className="sticky top-0 z-50 bg-white">
      <div className="relative flex h-14 items-center justify-between px-4">
        <div className="flex flex-1 items-start">
          {showBackButton && (
            <button onClick={handleBackPress} className="p-2">
              <IoArrowBack />
            </button>
          )}
        </div>

        <div className="flex flex-2 items-center justify-center">
          {title && (
            <h1 className="text-lg font-bold text-neutral-900">{title}</h1>
          )}
        </div>

        <div className="flex flex-1 items-end justify-end">{rightElement}</div>
        {showCancelButton && (
          <button
            onClick={onCancelPress}
            className="absolute top-0 right-0 p-4"
          >
            <span className="text-2xl text-neutral-900">x</span>
          </button>
        )}
      </div>
    </header>
  )
}

export default Header

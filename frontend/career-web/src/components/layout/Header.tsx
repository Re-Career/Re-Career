'use client'

import React from 'react'
import { usePathname, useRouter } from 'next/navigation'
import { GoArrowLeft } from 'react-icons/go'
import { deleteCookie, getCookie } from '@/app/actions/global/action'
import { GoHome as HomeIcon } from 'react-icons/go'
import { IoClose as CloseIcon } from 'react-icons/io5'

interface HeaderProps {
  title?: string
  showBackButton?: boolean
  showCancelButton?: boolean
  showHomeButton?: boolean
  onBackPress?: () => void
  onCancelPress?: () => void
  rightElement?: React.ReactNode
}

const Header: React.FC<HeaderProps> = ({
  title,
  showBackButton = false,
  showCancelButton = false,
  showHomeButton = false,
  onBackPress,
  onCancelPress: customOnCancelPress,
  rightElement,
}) => {
  const router = useRouter()
  const pathname = usePathname()

  const defaultOnCancelPress = async () => {
    let redirectUrl = await getCookie('redirectUrl')

    if (redirectUrl) {
      if (pathname === '/login') {
        redirectUrl = '/'
      }
      await deleteCookie('redirectUrl')

      router.replace(redirectUrl)
    } else {
      router.back()
    }
  }

  const handleCancelPress = customOnCancelPress || defaultOnCancelPress

  const handleBackPress = () => {
    router.back()

    if (onBackPress) {
      onBackPress()
    }
  }

  const handleHomePress = () => {
    router.push('/')
  }

  return (
    <header className="fixed top-0 right-0 left-0 z-70 mx-auto h-14 max-w-[450px] bg-white">
      <div className="relative flex h-14 items-center justify-between px-4">
        <div className="flex flex-1 items-start">
          {showBackButton && (
            <button onClick={handleBackPress} className="p-2">
              <GoArrowLeft className="h-6 w-6" />
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
            onClick={handleCancelPress}
            className="absolute top-0 right-0 p-4"
          >
            <CloseIcon className="h-6 w-6" />
          </button>
        )}
        {showHomeButton && (
          <button
            onClick={handleHomePress}
            className="absolute top-0 right-0 p-4"
          >
            <HomeIcon className="h-6 w-6" />
          </button>
        )}
      </div>
    </header>
  )
}

export default Header

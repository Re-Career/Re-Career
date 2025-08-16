'use client'

import React, { useMemo } from 'react'
import { GoHome, GoHomeFill, GoPerson, GoPersonFill } from 'react-icons/go'
import { BsPeople, BsPeopleFill } from 'react-icons/bs'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

const DEFAULT_TABS = [
  {
    key: 'home',
    name: '홈',
    icon: GoHome,
    filledIcon: GoHomeFill,
    href: '/',
  },
  {
    key: 'matching',
    name: '매칭',
    icon: BsPeople,
    filledIcon: BsPeopleFill,
    href: '/matching',
  },
  {
    key: 'my-page',
    name: 'MY',
    icon: GoPerson,
    filledIcon: GoPersonFill,
    href: 'my-page',
  },
]

type TabsKey = (typeof DEFAULT_TABS)[number]['key']

export default function Tabs() {
  const pathname = usePathname()

  const activeTab: TabsKey = useMemo(() => {
    const currentPage = pathname.split('/')[1]

    if (pathname === '/') {
      return 'home'
    } else if (currentPage === 'matching') {
      return 'matching'
    } else if (currentPage === 'my-page') {
      return 'my-page'
    }
    return 'home'
  }, [pathname])

  return (
    <div className="fixed right-0 bottom-0 left-0 z-50 mx-auto max-w-[450px] border-t border-gray-100 bg-white shadow">
      <div className="flex">
        {DEFAULT_TABS.map(
          ({ key, name, icon: Icon, filledIcon: FilledIcon, href }) => {
            const isActive = activeTab === key
            const IconComponent = isActive ? FilledIcon : Icon
            return (
              <Link
                key={key}
                className={`flex flex-1 flex-col items-center justify-center pt-3 pb-6 transition-colors ${
                  isActive ? 'text-gray-900' : 'text-gray-500'
                }`}
                href={href}
              >
                <IconComponent size={24} className="mb-1" />
                <span className="text-xs font-medium">{name}</span>
              </Link>
            )
          }
        )}
      </div>
    </div>
  )
}

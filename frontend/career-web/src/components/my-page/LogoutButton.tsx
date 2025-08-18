'use client'

import { clearTokens } from '@/app/actions/auth/action'
import { useRouter } from 'next/navigation'

export const LogoutButton = () => {
  const router = useRouter()

  const handleLogout = async () => {
    try {
      await clearTokens()
      router.push('/login')
    } catch (error) {
      console.error('로그아웃 실패:', error)
    }
  }

  return (
    <button onClick={handleLogout} className="text-left">
      로그아웃
    </button>
  )
}

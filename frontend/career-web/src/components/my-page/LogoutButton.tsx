'use client'

import { clearTokens } from '@/app/actions/auth/action'
import { useToast } from '@/hooks/useToast'
import { useRouter } from 'next/navigation'

export const LogoutButton = () => {
  const { showError } = useToast()
  const router = useRouter()

  const handleLogout = async () => {
    try {
      await clearTokens()
      router.push('/login')
    } catch (error) {
      showError(error as string)
    }
  }

  return (
    <button onClick={handleLogout} className="text-left">
      로그아웃
    </button>
  )
}

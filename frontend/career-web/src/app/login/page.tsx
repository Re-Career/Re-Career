'use client'

import { useRouter } from 'next/navigation'

export default function Login() {
  const router = useRouter()

  const handleOAuthLogin = () => {
    if (window) {
      const oauthUrl = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/kakao`

      if (window && oauthUrl) {
        window.location.href = oauthUrl
      }
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Re-Career 로그인
          </h2>
        </div>
        <div className="mt-8 space-y-6">
          <button
            onClick={handleOAuthLogin}
            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            OAuth로 로그인
          </button>
        </div>
      </div>
    </div>
  )
}

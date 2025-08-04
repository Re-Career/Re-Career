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
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Re-Career 로그인
          </h2>
        </div>
        <div className="mt-8 space-y-6">
          <button
            onClick={handleOAuthLogin}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 focus:outline-none"
          >
            OAuth로 로그인
          </button>
        </div>
      </div>
    </div>
  )
}

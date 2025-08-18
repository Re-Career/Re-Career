'use client'

import { useRouter } from 'next/navigation'

export default function Error({ reset }: { reset: () => void }) {
  const router = useRouter()

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 px-4">
      <div className="mb-6 flex flex-col items-center justify-center">
        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
          <svg
            className="h-8 w-8 text-red-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        </div>
        <h1 className="mb-2 text-2xl font-bold text-gray-900">
          오류가 발생했습니다
        </h1>
        <p className="text-center text-gray-600">
          예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.
        </p>
      </div>

      <div className="space-y-3">
        <button
          onClick={() => reset()}
          className="bg-primary-500 w-full rounded-lg px-4 py-3 font-medium"
        >
          다시 시도
        </button>

        <button
          onClick={() => router.replace('/')}
          className="bg-secondary-500 w-full rounded-lg px-4 py-3 font-medium"
        >
          홈으로 돌아가기
        </button>
      </div>
    </div>
  )
}

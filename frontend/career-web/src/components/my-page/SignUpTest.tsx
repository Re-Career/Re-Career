'use client'

import { useRouter } from 'next/navigation'

const SignUpTest = () => {
  const router = useRouter()

  const handleSignUp = () => {
    router.push(
      `/oauth2/redirect?accessToken=${process.env.NEXT_PUBLIC_TEMP_ACCESS_TOKEN}&refreshToken=${process.env.NEXT_PUBLIC_TEMP_ACCESS_TOKEN}`
    )
  }

  return (
    <button onClick={handleSignUp} className="rounded-lg border px-4 py-2">
      회원가입 테스트
    </button>
  )
}

export default SignUpTest

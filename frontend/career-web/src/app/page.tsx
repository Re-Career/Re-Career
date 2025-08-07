'use client'

import Header from '@/components/common/Header'
import { useRouter } from 'next/navigation'

export default function Home() {
  const router = useRouter()

  return (
    <>
      <Header title="Re:Career" />
      <button onClick={() => router.push('/sign-up')}> 회원가입</button>
      <main className="flex-1"></main>
    </>
  )
}

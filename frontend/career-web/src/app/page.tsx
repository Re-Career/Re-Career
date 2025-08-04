'use client'

import { useRouter } from 'next/navigation'

export default function Home() {
  const router = useRouter()

  const handleLogin = () => {
    if (window.ReactNativeWebView) {
      window.ReactNativeWebView.postMessage(JSON.stringify({ action: 'login' }))
    } else {
      router.push('/login')
    }
  }

  return (
    <div>
      <main>
        <button onClick={handleLogin} className="border bg-blue-300 px-4 py-2">
          로그인 테스트
        </button>
      </main>
      <footer></footer>
    </div>
  )
}

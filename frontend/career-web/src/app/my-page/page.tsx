'use client'

import Header from '@/components/common/Header'
import { sendMessageToNative, WebViewMessageTypes } from '@/utils/webview'
import { useRouter } from 'next/navigation'

const MyPagePage = () => {
  const router = useRouter()

  const handleLogin = () => {
    if (window.ReactNativeWebView) {
      sendMessageToNative({ type: WebViewMessageTypes.LOGIN })
    } else {
      router.push('/login')
    }
  }

  return (
    <>
      <Header title="마이페이지" />
      <main className="flex-1">
        <div></div>
        <button onClick={handleLogin} className="border bg-blue-300 px-4 py-2">
          로그인 테스트
        </button>
      </main>
    </>
  )
}

export default MyPagePage

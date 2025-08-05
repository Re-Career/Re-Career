'use client'

import Header from '@/components/common/Header'
import Image from 'next/image'
import { useRouter } from 'next/navigation'

export default function Login() {
  const router = useRouter()

  const handleOAuthLogin = () => {
    // if (window) {
    //   const oauthUrl = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/kakao`

    //   if (window && oauthUrl) {
    //     window.location.href = oauthUrl
    //   }
    // }
    router.push('/sign-up')
  }

  return (
    <div>
      <Header title="로그인" showCancelButton />
      <main className="mt-25 flex flex-col items-center justify-center">
        <Image src="/logo_lg.png" alt="login_logo" width={260} height={240} />
        <div className="my-10 flex flex-col items-center justify-center">
          <p>일을 넘어 삶을 설계하는 </p>
          <p>진짜 커리어 탐색 Re:Career</p>
        </div>
        <Image 
          src="/kakao_login.png" 
          alt="kakao_login" 
          width={300} 
          height={45} 
          onClick={handleOAuthLogin}
          className="cursor-pointer"
        />
      </main>
    </div>
  )
}

'use client'

import BottomSheet from './BottomSheet'
import { useLoginSheet } from '@/store/useLoginSheet'
import Image from 'next/image'

const GlobalLoginSheet = () => {
  const { isOpen, onClose } = useLoginSheet()

  const handleOAuthLogin = () => {
    if (window) {
      const oauthUrl = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/kakao`

      if (window && oauthUrl) {
        return (window.location.href = oauthUrl)
      }
    }
    onClose()
  }

  return (
    <BottomSheet isOpen={isOpen} onClose={onClose}>
      <div className="my-10 flex flex-col items-center justify-center p-4 text-center">
        <Image src="/logo_lg.png" alt="login_logo" width={200} height={160} />
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
      </div>
    </BottomSheet>
  )
}

export default GlobalLoginSheet

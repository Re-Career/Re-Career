import { useSearchParams } from 'next/navigation'

const AuthRedirectPage = () => {
  const searchParams = useSearchParams()

  const accessToken = searchParams.get('accessToken')
  const refreshToken = searchParams.get('refreshToken')

  if (!accessToken || !refreshToken) {
    return console.log('토큰 정보 없음')
  }

  console.log(accessToken, refreshToken)

  return
}

export default AuthRedirectPage

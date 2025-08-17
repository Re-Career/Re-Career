import { redirect } from 'next/navigation'
import { RoleTypes } from '@/lib/constants/global'
import WebViewHandler from '@/components/oauth2/WebViewHandler'
import { getCookie, deleteCookie } from '@/app/actions/global/action'
import { setPendingToken, setToken } from '@/app/actions/auth/action'
import { getAuthMe } from '@/services/auth'

interface AuthRedirectPageProps {
  searchParams: Promise<{
    accessToken?: string
    refreshToken?: string
  }>
}

const AuthRedirectPage = async ({ searchParams }: AuthRedirectPageProps) => {
  const { accessToken, refreshToken } = await searchParams

  if (!accessToken || !refreshToken) {
    redirect('/login')
  }

  try {
    const auth = await getAuthMe(accessToken)

    //가입을 안한 유저
    if (!auth.data.signupCompleted) {
      //임시 토큰 설정
      await setPendingToken(accessToken, refreshToken)

      redirect(`/sign-up/${RoleTypes.MENTEE}`)
    }

    //토큰 설정
    await setToken(accessToken, refreshToken)

    const redirectUrl = (await getCookie('redirectUrl')) || '/'

    await deleteCookie('redirectUrl')

    return (
      <>
        <WebViewHandler
          accessToken={accessToken}
          refreshToken={refreshToken}
          redirectUrl={redirectUrl}
        />
      </>
    )
  } catch {
    redirect('/login')
  }
}

export default AuthRedirectPage

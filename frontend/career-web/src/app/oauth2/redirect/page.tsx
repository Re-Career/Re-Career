import { sendMessageToNative, WebViewMessageTypes } from '@/utils/webview'
import { redirect } from 'next/navigation'

const AuthRedirectPage = async ({
  searchParams,
}: {
  searchParams: { [key: string]: string | string[] | undefined }
}) => {
  const { accessToken, refreshToken } = await searchParams

  try {
    const data = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/me`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    })

    const auth = await data.json()

    if (!auth.signupCompleted) {
      redirect('/sign-up')
    } else {
      sendMessageToNative({ type: WebViewMessageTypes.CLOSE_WEBVIEW })
    }
  } catch (error) {
    throw new Error('Authentication failed')
  }
}

export default AuthRedirectPage

import Header from '@/components/common/Header'
import { LoginButton, LogoutButton, SignUpTest } from '@/components/my-page'
import { getToken } from '../actions/auth/action'

const MyPagePage = async () => {
  const accessToken = await getToken()

  return (
    <>
      <Header title="마이페이지" />
      <main className="flex-1">
        {accessToken ? (
          <LogoutButton />
        ) : (
          <div className="flex gap-1">
            <LoginButton />
            <SignUpTest />
          </div>
        )}
      </main>
    </>
  )
}

export default MyPagePage

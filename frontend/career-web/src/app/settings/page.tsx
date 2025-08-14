import { getToken } from '@/app/actions/auth/action'
import { LoginButton, LogoutButton, SignUpTest } from '@/components/my-page'
import React from 'react'

const page = async () => {
  const accessToken = await getToken()
  return (
    <div>
      {accessToken ? (
        <LogoutButton />
      ) : (
        <div className="flex gap-1">
          <LoginButton />
          <SignUpTest />
        </div>
      )}
    </div>
  )
}

export default page

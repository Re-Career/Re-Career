import { getTokens } from '@/app/actions/auth/action'
import LogoutButton from '@/components/settings/LogoutButton'

import React from 'react'

const page = async () => {
  const { accessToken } = await getTokens()

  return <div>{accessToken && <LogoutButton />}</div>
}

export default page

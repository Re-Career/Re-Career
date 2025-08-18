'use server'

import { deleteCookie, getCookie } from '@/app/actions/global/action'

const tokenList = ['accessToken', 'refreshToken']
const pendingTokenList = ['pendingAccessToken', 'pendingRefreshToken']

export const getTokens = async () => {
  const [accessToken, refreshToken] = await Promise.all(
    tokenList.map((key) => getCookie(key))
  )

  return { accessToken, refreshToken }
}

export const clearTokens = async () => {
  return tokenList.forEach((key) => deleteCookie(key))
}

export const getPendingTokens = async () => {
  const [pendingAccessToken, pendingRefreshToken] = await Promise.all(
    pendingTokenList.map((key) => getCookie(key))
  )

  return { pendingAccessToken, pendingRefreshToken }
}

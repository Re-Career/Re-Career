'use server'

import { deleteCookie, getCookie, setCookie } from '@/app/actions/global/action'

export const getToken = async (): Promise<string> => {
  const token = await getCookie('accessToken')

  return token ?? ''
}

export const clearToken = async () => {
  const deleteList = ['accessToken', 'refreshToken']

  return deleteList.forEach((key) => deleteCookie(key))
}

export const setToken = async (accessToken: string, refreshToken: string) => {
  await setCookie('accessToken', accessToken, {
    maxAge: 24 * 60 * 60,
  })
  await setCookie('refreshToken', refreshToken, {
    maxAge: 7 * 24 * 60 * 60,
  })

  return
}

export const setPendingToken = async (
  accessToken: string,
  refreshToken: string
) => {
  await setCookie('pendingAccessToken', accessToken, {
    maxAge: 24 * 60 * 60,
  })
  await setCookie('pendingRefreshToken', refreshToken, {
    maxAge: 7 * 24 * 60 * 60,
  })

  return
}

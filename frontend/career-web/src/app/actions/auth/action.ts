'use server'

import { deleteCookie, getCookie } from '@/app/actions/global/action'

const tokenList = ['accessToken', 'refreshToken']

export const getToken = async (): Promise<string> => {
  const token = await getCookie('accessToken')

  return token ?? ''
}

export const clearToken = async () => {
  return tokenList.forEach((key) => deleteCookie(key))
}

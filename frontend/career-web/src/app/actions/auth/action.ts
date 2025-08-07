'use server'

import { cookies } from 'next/headers'

export const getToken = async (): Promise<string> => {
  const cookieStore = await cookies()

  return cookieStore.get('accessToken')?.value || ''
}

export const clearToken = async () => {
  const cookieStore = await cookies()

  const deleteList = ['accessToken', 'refreshToken']

  return deleteList.forEach((key) => cookieStore.delete(key))
}

'use server'

import { cookies } from 'next/headers'
import type { ResponseCookie } from 'next/dist/compiled/@edge-runtime/cookies'

export const setCookie = async ({
  name,
  value,
  options,
}: {
  name: string
  value: string
  options: Partial<ResponseCookie>
}) => {
  const cookieStore = await cookies()

  cookieStore.set(name, value, {
    httpOnly: options.httpOnly ?? false,
    secure: options.secure ?? process.env.NODE_ENV === 'production',
    sameSite: options.sameSite ?? 'strict',
    maxAge: options.maxAge,
    path: options.path ?? '/',
    ...options,
  })
}

export const getCookie = async (name: string) => {
  const cookieStore = await cookies()

  return cookieStore.get(name)?.value
}

export const deleteCookie = async (name: string) => {
  const cookieStore = await cookies()

  cookieStore.delete(name)
}

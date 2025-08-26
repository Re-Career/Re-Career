import { DefaultSignUpFormData } from '@/types/auth'
import { fetchUrl } from '../api'
import { User } from '@/types/user'
import { FetchResponse } from '@/types/global'
import { clearTokens, getTokens } from '@/app/actions/auth/action'
import { setCookie } from '@/app/actions/global/action'
import { ONE_DAY } from '@/lib/constants/global'

export const getAuthMe = async (
  accessToken: string
): Promise<FetchResponse<User>> => {
  return await fetchUrl<User>('/auth/me', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
}

export const refreshAccessToken = async (): Promise<FetchResponse<string>> => {
  const { refreshToken } = await getTokens()

  const res = await fetchUrl<string>('/auth/refresh', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${refreshToken}`,
    },
  })

  if (res.data) {
    setCookie({
      name: 'accessToken',
      value: res.data,
      options: {
        maxAge: ONE_DAY,
      },
    })
  }

  if (res.errorMessage) {
    await clearTokens()
  }

  return res
}

export const postSignUp = async ({
  accessToken,
  formData,
}: {
  accessToken: string
  formData: DefaultSignUpFormData
}): Promise<FetchResponse<User>> => {
  return await fetchUrl<User>('/auth/signup', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(formData),
  })
}

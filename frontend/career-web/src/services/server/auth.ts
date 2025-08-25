import { DefaultSignUpFormData } from '@/types/auth'
import { fetchUrl } from '../api'
import { User } from '@/types/user'
import { FetchResponse } from '@/types/global'

export const getAuthMe = async (
  accessToken: string
): Promise<FetchResponse<User>> => {
  return await fetchUrl<User>('/auth/me', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
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

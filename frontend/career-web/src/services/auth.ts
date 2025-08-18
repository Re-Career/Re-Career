import { SignUpFormData } from '@/types/auth'
import { fetchUrl } from './api'

export const getAuthMe = async (accessToken: string) => {
  const { data } = await fetchUrl('/auth/me', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  return data
}

export const postSignUp = async ({
  accessToken,
  requestData,
}: {
  accessToken: string
  requestData: SignUpFormData
}) => {
  return await fetchUrl('/auth/signup', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(requestData),
  })
}

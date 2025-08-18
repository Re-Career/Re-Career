import { SignUpFormData } from '@/types/auth'
import { fetchUrl } from './api'
import { User } from '@/types/user'
import { FetchResponse } from '@/types/global'

export const getAuthMe = async (
  accessToken: string
): Promise<FetchResponse<User>> => {
  const res = await fetchUrl('/auth/me', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  const isSuccess = res.ok
  const responseData = await res.json().catch(() => ({}))
  const errorData = responseData
  const data = responseData

  let errorMessage: string = ''

  if (errorData.errors && typeof errorData.errors === 'object') {
    errorMessage = errorData.message || '입력 정보를 확인해주세요.'
  }

  return { isSuccess, errorMessage, data }
}

export const postSignUp = async ({
  accessToken,
  requestData,
}: {
  accessToken: string
  requestData: SignUpFormData
}) => {
  const res = await fetchUrl('/auth/signup', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(requestData),
  })
  
  return res
}

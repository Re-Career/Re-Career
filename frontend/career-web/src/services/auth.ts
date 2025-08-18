import { DefaultSignUpFormData } from '@/types/auth'
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
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors
    errorMessage = data?.message || `접근 권한이 없습니다.`
  }

  return { errorMessage, data: data.data, errors }
}

export const postSignUp = async ({
  accessToken,
  formData,
}: {
  accessToken: string
  formData: DefaultSignUpFormData
}): Promise<FetchResponse<User>> => {
  const res = await fetchUrl('/auth/signup', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(formData),
  })

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors

    errorMessage =
      errors && typeof errors === 'object'
        ? data?.message || '입력 정보를 확인해주세요.'
        : data?.message || `회원가입에 실패했습니다.`
  }

  return { errorMessage, data: data.data, errors, status: res.status }
}

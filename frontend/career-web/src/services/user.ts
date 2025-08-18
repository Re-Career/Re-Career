import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from './api'
import { PutUserPayload, User } from '@/types/user'
import { FetchResponse, ResponseMessage } from '@/types/global'

export const getUserProfile = async (): Promise<User> => {
  const { accessToken } = await getTokens()

  const res = await fetchUrl('/user/profile', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  })

  const { data } = await res.json()

  return data
}

export const putProfileImage = async ({
  accessToken,
  imageFile,
}: {
  accessToken?: string
  imageFile: File
}): Promise<FetchResponse<ResponseMessage>> => {
  let token = accessToken

  if (!token) {
    const { accessToken } = await getTokens()

    token = accessToken
  }

  const formData = new FormData()

  formData.append('file', imageFile)

  const res = await fetchUrl('/user/profile/image', {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  })

  const isSuccess = res.ok

  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors
    errorMessage = data?.message || '이미지 업로드에 실패했습니다.'
  }

  return { errorMessage, data: data.data, errors }
}

export const deleteProfileImage = async (
  accessToken?: string
): Promise<FetchResponse<ResponseMessage>> => {
  let token = accessToken

  if (!token) {
    const { accessToken: tokenFromCookies } = await getTokens()

    token = tokenFromCookies
  }

  const res = await fetchUrl('/user/profile/image', {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''

  if (!isSuccess) {
    errorMessage = data?.message || '이미지 삭제에 실패했습니다.'
  }

  return { errorMessage, data: data.data }
}

export const putUser = async ({
  accessToken,
  userData,
}: {
  accessToken?: string
  userData: PutUserPayload
}): Promise<FetchResponse<ResponseMessage>> => {
  let token = accessToken

  if (!token) {
    const { accessToken } = await getTokens()

    token = accessToken
  }

  const res = await fetchUrl('/user/profile', {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(userData),
  })

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors
    errorMessage = data?.message || '프로필 업데이트에 실패했습니다.'
  }

  return { errorMessage, data: data.data, errors, status: res.status }
}

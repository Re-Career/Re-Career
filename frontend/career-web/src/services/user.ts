import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from './api'
import { User } from '@/types/user'
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

  const res = await fetchUrl('/user/profile/image', {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: imageFile,
  })

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors
    errorMessage = data?.message || '이미지 업로드에 실패했습니다.'
  }

  return { errorMessage, data, errors }
}

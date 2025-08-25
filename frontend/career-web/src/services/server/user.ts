import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from '../api'
import { PutUserPayload, User } from '@/types/user'
import { FetchResponse, ResponseMessage } from '@/types/global'
import { ONE_DAY } from '@/lib/constants/global'

export const getUserProfile = async (): Promise<FetchResponse<User>> => {
  const { accessToken } = await getTokens()

  return await fetchUrl<User>('/user/profile', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
    next: { revalidate: ONE_DAY },
  })
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

  return await fetchUrl<ResponseMessage>('/user/profile/image', {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  })
}

export const deleteProfileImage = async (
  accessToken?: string
): Promise<FetchResponse<ResponseMessage>> => {
  let token = accessToken

  if (!token) {
    const { accessToken: tokenFromCookies } = await getTokens()

    token = tokenFromCookies
  }

  return await fetchUrl<ResponseMessage>('/user/profile/image', {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
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

  return await fetchUrl<ResponseMessage>('/user/profile', {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(userData),
  })
}

export const putUserPersonalityTags = async ({
  accessToken,
  personalityTagIds,
}: {
  accessToken?: string
  personalityTagIds: number[]
}): Promise<FetchResponse<ResponseMessage>> => {
  let token = accessToken

  if (!token) {
    const { accessToken } = await getTokens()

    token = accessToken
  }

  return await fetchUrl<ResponseMessage>('/user/personality-tags', {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ personalityTagIds }),
  })
}

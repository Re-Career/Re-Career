import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'
import {
  PostSessionPayload,
  PostSessionResponse,
  Session,
} from '@/types/session'
import { getUserProfile } from './user'

export const getSession = async (
  id: string
): Promise<FetchResponse<Session>> => {
  const { accessToken } = await getTokens()
  const user = await getUserProfile()

  const res = await fetchUrl(`/sessions/${id}?role=${user.role}`, {
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
    errorMessage = data?.message || `해당 상담이 없습니다.`
  }

  return { errorMessage, data: data.data, errors, status: res.status }
}

export const postSession = async (
  payload: PostSessionPayload
): Promise<FetchResponse<PostSessionResponse>> => {
  const { accessToken } = await getTokens()

  const res = await fetchUrl('/sessions', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(payload),
  })

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors ?? {}
    errorMessage = data?.message || '상담 신청에 실패했습니다.'
  }

  return {
    errorMessage,
    data: data.data,
    errors,
    status: res.status,
  }
}

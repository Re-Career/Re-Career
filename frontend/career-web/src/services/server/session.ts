import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'
import {
  PostSessionPayload,
  PostSessionResponse,
  Session,
  SessionResponse,
} from '@/types/session'
import { getUserProfile } from './user'
import dayjs from 'dayjs'
import 'dayjs/locale/ko'

dayjs.locale('ko')

export const getSession = async (
  id: string
): Promise<FetchResponse<Session>> => {
  const { accessToken } = await getTokens()
  const user = await getUserProfile()

  return await fetchUrl<Session>(`/sessions/${id}?role=${user.role}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
}

export const getSessionList = async (): Promise<FetchResponse<Session[]>> => {
  const { accessToken } = await getTokens()
  const user = await getUserProfile()

  const response = await fetchUrl<SessionResponse[]>(`/sessions?role=${user.role}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  if (response.errorMessage) {
    return { ...response, data: [] }
  }

  const _data = response.data.map((session: SessionResponse) => {
    return {
      id: session.sessionId,
      sessionTime: session.sessionTime,
      status: session.status,
      mentor: session.mentor,
      mentee: session.mentee,
    }
  })

  return { ...response, data: _data }
}

export const postSession = async (
  payload: PostSessionPayload
): Promise<FetchResponse<PostSessionResponse>> => {
  const { accessToken } = await getTokens()

  return await fetchUrl<PostSessionResponse>('/sessions', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${accessToken}2`,
    },
    body: JSON.stringify(payload),
  })
}

import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'
import {
  PostSessionPayload,
  PostSessionResponse,
  Session,
  SessionResponse,
} from '@/types/session'
import { decodeJWT } from '@/lib/utils/jwt'
import dayjs from 'dayjs'
import 'dayjs/locale/ko'

dayjs.locale('ko')

export const getSession = async (
  id: string
): Promise<FetchResponse<Session>> => {
  const { accessToken } = await getTokens()

  if (!accessToken) {
    return {
      errorMessage: 'No access token',
      data: null as any,
      errors: {},
      status: 401,
    }
  }

  const payload = decodeJWT(accessToken)

  if (!payload) {
    return {
      errorMessage: 'Invalid token',
      data: null as any,
      errors: {},
      status: 401,
    }
  }

  const role = payload.role.replace('ROLE_', '')
  
  return await fetchUrl<Session>(`/sessions/${id}?role=${role}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
}

export const getSessionList = async (): Promise<FetchResponse<Session[]>> => {
  const { accessToken } = await getTokens()

  if (!accessToken) {
    return {
      errorMessage: 'No access token',
      data: [],
      errors: {},
      status: 401,
    }
  }

  const payload = decodeJWT(accessToken)

  if (!payload) {
    return { errorMessage: 'Invalid token', data: [], errors: {}, status: 401 }
  }

  const role = payload.role.replace('ROLE_', '')
  
  const response = await fetchUrl<SessionResponse[]>(
    `/sessions?role=${role}`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  )

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

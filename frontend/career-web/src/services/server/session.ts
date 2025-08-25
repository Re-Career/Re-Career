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

  const res = await fetchUrl(`/session/${id}?role=${user.role}`, {
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

export const getSessionList = async (): Promise<FetchResponse<Session[]>> => {
  const { accessToken } = await getTokens()
  const user = await getUserProfile()

  const res = await fetchUrl(`/sessions?role=${user.role}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  const isSuccess = res.ok
  const jsonData = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = jsonData?.errors
    errorMessage = jsonData?.message || `상담리스트 로딩에 실패했습니다.`
  }

  const { data } = jsonData

  const _data = data.map((session: SessionResponse) => {
    const formattedTime = dayjs(session.sessionTime).format(
      'YYYY년 M월 D일 A h:mm'
    )

    return {
      id: session.sessionId,
      sessionTime: session.sessionTime,
      sessionTimeFormatted: formattedTime,
      status: session.status,
      mentor: session.mentor,
      mentee: session.mentee,
    }
  })

  return { errorMessage, data: _data, errors, status: res.status }
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

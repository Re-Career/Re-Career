import { getTokens } from '@/app/actions/auth/action'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'

interface FormData {
  mentorId: number
  userId: number
  sessionTime: string
}

export const postSession = async (
  formData: FormData
): Promise<FetchResponse<{ id: number }>> => {
  const { accessToken } = await getTokens()

  const res = await fetchUrl('/sessions', {
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
    errors = data?.errors ?? {}
    errorMessage = data?.message || '상담 신청에 실패했습니다.'
  }

  return {
    errorMessage,
    data: { id: 1 },
    errors,
    status: res.status,
  }
}

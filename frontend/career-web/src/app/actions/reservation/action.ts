'use server'

import { postSession } from '@/services/server/session'
import { PostSessionResponse } from '@/types/session'
import { revalidateTag } from 'next/cache'

interface FormState {
  success: boolean
  message: string
  data?: PostSessionResponse
  errors?: Record<string, string>
  status?: number
}

export const handleCreateSession = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const mentorId = formData.get('mentorId') as string
  const sessionTime = formData.get('sessionTime') as string

  const sessionData = {
    mentorId: Number(mentorId),
    sessionTime,
  }

  const result = await postSession(sessionData)

  if (result.status === 401) {
    return {
      success: false,
      status: result.status,
      message: '재로그인이 필요합니다.',
    }
  }

  if (result.errorMessage) {
    return {
      success: false,
      message: result.errorMessage,
      errors: result.errors,
    }
  }

  revalidateTag('session-list')

  return {
    success: true,
    data: result.data,
    message: '상담예약에 성공했습니다.',
  }
}

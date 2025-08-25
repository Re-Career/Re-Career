'use server'

import { postSession } from '@/services/server/session'
import { PostSessionResponse } from '@/types/session'

interface FormState {
  success: boolean
  message: string
  data?: PostSessionResponse
  errors?: Record<string, string>
}

export const handleCreateSession = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const mentorId = formData.get('mentorId') as string
  const sessionTime = formData.get('sessionTime') as string

  console.log({ sessionTime })
  const { errorMessage, errors, data, status } = await postSession({
    mentorId: Number(mentorId),
    sessionTime,
  })

  if (status === 401 || errorMessage) {
    const baseResponse: FormState = {
      success: false,
      message: errorMessage,
    }

    if (errors) {
      baseResponse.errors = errors
    }

    return baseResponse
  }

  return {
    success: true,
    data,
    message: '상담예약에 성공했습니다.',
  }
}

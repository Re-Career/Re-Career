'use server'

import { redirect } from 'next/navigation'

interface FormState {
  success: boolean
  message: string
  error?: string
}

export const handleReserve = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  // const dateTime = formData.get('dateTime') as string
  // const emailAlert = formData.get('emailAlert') === 'on'
  const mentorId = formData.get('mentorId') as string

  // 실제 예약 로직을 여기에 추가
  // API 호출 등...

  redirect(`/mentor/${mentorId}/reservation/success`)
}

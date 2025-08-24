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
  const mentorId = formData.get('mentorId') as string

  redirect(`/mentor/${mentorId}/reservation/success`)
}

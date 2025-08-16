'use server'

import { OneDay } from '@/lib/constants/global'
import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'
import z from 'zod'

interface FormState {
  success: boolean
  message: string
  authInfo?: { accessToken: string; refreshToken: string }
  data?: { name: string; email: string; interestTags: string[] }
  error?: string
}

export const signUpAction = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const cookieStore = await cookies()

  const name = formData.get('name') as string
  const role = formData.get('role') as string
  const email = formData.get('email') as string
  const profileImage = formData.get('profileImage') as File
  // TODO: 관심태그 url 통일 작업후 추가
  // const interestTags = formData.getAll('interestTags') as string[]

  const schema = z.strictObject({
    name: z.string(),
    email: z.string(),
    role: z.string(),
    profileImageUrl: z.string(),
  })

  try {
    const accessToken = cookieStore.get('pendingAccessToken')?.value
    const refreshToken = cookieStore.get('pendingRefreshToken')?.value

    if (!accessToken || !refreshToken) {
      throw new Error('회원 정보가 없습니다.')
    }

    const formData = {
      name,
      email,
      role,
      profileImageUrl: profileImage,
    }

    const parseResult = schema.safeParse(formData)
    
    if (!parseResult.success) {
      throw new z.ZodError(parseResult.error.issues)
    }

    await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/signup`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    })

    cookieStore.set({
      name: 'accessToken',
      value: accessToken,
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      maxAge: OneDay,
      path: '/',
    })

    cookieStore.set({
      name: 'refreshToken',
      value: refreshToken,
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      maxAge: 7 * OneDay,
      path: '/',
    })

    redirect('/sign-up/success')
  } catch (error) {
    if (error instanceof z.ZodError) {
      console.error('Validation errors:', error.issues)
    }

    return {
      success: false,
      message: '회원가입에 실패했습니다. 다시 시도해주세요.',
      error: error instanceof Error ? error.message : 'Unknown error',
    }
  }
}

'use server'

import { OneDay } from '@/lib/constants/global'
import { cookies } from 'next/headers'
import z from 'zod'

interface FormState {
  success: boolean
  message: string
  authInfo?: { accessToken: string; refreshToken: string }
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
  const profileImageUrl = formData.get('profileImageUrl') as string
  const personalityTagIds = formData.getAll('personalityTagIds') as string[]

  const schema = z.strictObject({
    name: z.string(),
    email: z.string(),
    role: z.string(),
    profileImageUrl: z.string().nullable().optional(),
    personalityTagIds: z.array(z.string()),
  })

  try {
    if (personalityTagIds.length === 0) {
      throw new Error('태그를 한 개 이상 선택해야합니다.')
    }

    const accessToken = cookieStore.get('pendingAccessToken')?.value
    const refreshToken = cookieStore.get('pendingRefreshToken')?.value

    if (!accessToken || !refreshToken) {
      throw new Error('회원 정보가 없습니다.')
    }

    const formData = {
      name,
      email,
      role,
      profileImageUrl,
      personalityTagIds,
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

    cookieStore.delete('pendingAccessToken')
    cookieStore.delete('pendingRefreshToken')

    return {
      success: true,
      message: '회원가입에 성공했습니다.',
    }
  } catch (error) {
    if (error instanceof z.ZodError) {
      const fieldErrors = error.issues.map((issue) => {
        const field = issue.path.join('.')
        switch (field) {
          case 'name':
            return '이름을 입력해주세요.'
          case 'email':
            return '이메일을 입력해주세요.'
          case 'role':
            return '역할을 선택해주세요.'
          case 'profileImageUrl':
            return '프로필 이미지를 업로드해주세요.'
          case 'personalityTagIds':
            return '성격 태그를 선택해주세요.'
          default:
            return `${field}: ${issue.message}`
        }
      })

      return {
        success: false,
        message: fieldErrors.join(' '),
        error: error.issues
          .map((issue) => `${issue.path.join('.')}: ${issue.message}`)
          .join(', '),
      }
    }

    return {
      success: false,
      message: '회원가입에 실패했습니다. 다시 시도해주세요.',
      error: error instanceof Error ? error.message : 'Unknown error',
    }
  }
}

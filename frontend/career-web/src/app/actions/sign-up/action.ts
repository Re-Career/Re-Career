'use server'

import { OneDay } from '@/lib/constants/global'
import { postSignUp } from '@/services/auth'
import { SignUpFormData } from '@/types/auth'
import { cookies } from 'next/headers'
import z from 'zod'

interface FormState {
  success: boolean
  message: string
  formData: {
    name: string
    email: string
  }
  errors?: Record<string, string>
}

export const signUpAction = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const cookieStore = await cookies()

  const name = formData.get('name') as string
  const role = formData.get('role') as string
  const email = formData.get('email') as string
  const region = formData.get('region') as string
  const profileImageUrl = formData.get('profileImageUrl') as string
  const personalityTagIds = formData.getAll('personalityTagIds') as string[]

  const schema = z.object({
    name: z.string().min(1, '이름을 입력해주세요.'),
    email: z.email('올바른 이메일 형식을 입력해주세요.'),
    role: z.string().min(1, '역할을 선택해주세요.'),
    region: z.string().min(1, '지역을 선택해주세요.'),
    profileImageUrl: z.string().nullable().optional(),
    personalityTagIds: z
      .array(z.string())
      .min(1, '성격 태그를 최소 1개 이상 선택해주세요.'),
  })

  try {
    const accessToken = cookieStore.get('pendingAccessToken')?.value
    const refreshToken = cookieStore.get('pendingRefreshToken')?.value

    if (!accessToken || !refreshToken) {
      throw new Error('회원 정보가 없습니다.')
    }

    const requestData: SignUpFormData = {
      name,
      email,
      role,
      region,
      profileImageUrl,
      personalityTagIds,
    }

    const parseResult = schema.safeParse(requestData)

    if (!parseResult.success) {
      throw new z.ZodError(parseResult.error.issues)
    }

    const res = await postSignUp({ accessToken, requestData })

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}))

      if (errorData.errors && typeof errorData.errors === 'object') {
        return {
          success: false,
          message: errorData.message || '입력 정보를 확인해주세요.',
          errors: errorData.errors,
          formData: { name, email },
        }
      }

      return {
        success: false,
        message:
          errorData.message ||
          `회원가입에 실패했습니다. 상태코드: ${res.status}`,
        formData: { name, email },
      }
    }

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
      formData: { name, email },
    }
  } catch (error) {
    if (error instanceof z.ZodError) {
      const fieldErrors: Record<string, string> = {}

      error.issues.forEach((issue) => {
        const fieldName = issue.path[0] as string

        fieldErrors[fieldName] = issue.message || '유효하지 않은 값입니다.'
      })

      return {
        success: false,
        message: '입력 정보를 확인해주세요.',
        errors: fieldErrors,
        formData: { name, email },
      }
    }

    return {
      success: false,
      message: '회원가입에 실패했습니다. 다시 시도해주세요.',
      formData: { name, email },
    }
  }
}

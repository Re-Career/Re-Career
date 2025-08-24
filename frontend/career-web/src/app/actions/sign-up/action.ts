'use server'

import { ONE_DAY } from '@/lib/constants/global'
import { postSignUp } from '@/services/server/auth'
import { SignUpFormData } from '@/types/auth'
import z from 'zod'
import { setCookie } from '../global/action'
import { putProfileImage } from '@/services/server/user'
import { clearPendingTokens, getPendingTokens } from '../auth/action'

interface DefaultFormData {
  name: string
  email: string
}

interface FormState {
  success: boolean
  message: string
  formData: DefaultFormData
  status?: number
  errors?: Record<string, string>
}

const handleErrorResponse = ({
  errorMessage,
  defaultFormData,
  status,
  errors,
}: {
  errorMessage: string
  defaultFormData: DefaultFormData
  status?: number
  errors?: Record<string, string>
}): FormState => {
  const baseResponse: FormState = {
    success: false,
    message: errorMessage,
    formData: defaultFormData,
  }

  if (status) {
    baseResponse.status = status
  }

  if (errors) {
    baseResponse.errors = errors
  }

  return baseResponse
}

export const signUpAction = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const name = formData.get('name') as string
  const role = formData.get('role') as string
  const email = formData.get('email') as string
  const provinceId = formData.get('provinceId') as string
  const cityId = formData.get('cityId') as string
  const profileImageFile = formData.get('profileImageFile') as File
  const personalityTagIds = formData.getAll('personalityTagIds') as string[]
  const position = formData.get('position') as string
  const description = formData.get('description') as string

  const schema = z.object({
    name: z.string().min(1, '이름을 입력해주세요.'),
    email: z.email('올바른 이메일 형식을 입력해주세요.'),
    role: z.string().min(1, '역할을 선택해주세요.'),
    provinceId: z.number().min(1, '지역을 선택해주세요.'),
    cityId: z.number().optional(),
    profileImageFile: z.file(),
    personalityTagIds: z
      .array(z.number())
      .min(1, '성격 태그를 최소 1개 이상 선택해주세요.')
      .max(5, '성격 태그는 최대 5개까지 선택할 수 있습니다.'),
    position: z.string().optional(),
    description: z.string().optional(),
  })

  const defaultFormData = { name, email }

  try {
    const { pendingAccessToken, pendingRefreshToken } = await getPendingTokens()

    if (!pendingAccessToken || !pendingRefreshToken) {
      return handleErrorResponse({
        errorMessage: '회원 정보가 없습니다.',
        status: 401,
        defaultFormData,
      })
    }

    if (!provinceId) {
      return handleErrorResponse({
        errorMessage: '지역을 선택해주세요.',
        defaultFormData,
      })
    }

    const requestData: SignUpFormData = {
      name,
      email,
      role,
      provinceId: Number(provinceId),
      ...(cityId && { cityId: Number(cityId) }),
      profileImageFile,
      personalityTagIds: personalityTagIds.map(id => Number(id)),
      ...(role === 'MENTOR' && position && { position }),
      ...(role === 'MENTOR' && description && { description }),
    }

    const parseResult = schema.safeParse(requestData)

    if (!parseResult.success) {
      throw new z.ZodError(parseResult.error.issues)
    }

    const { profileImageFile: imageFile, ...signUpFormData } = requestData

    if (imageFile && imageFile.size > 0) {
      const { errorMessage, errors, status } = await putProfileImage({
        accessToken: pendingAccessToken,
        imageFile,
      })

      if (status === 401 || errorMessage) {
        return handleErrorResponse({
          errorMessage,
          status,
          errors,
          defaultFormData,
        })
      }
    }

    const { errorMessage, errors, status } = await postSignUp({
      accessToken: pendingAccessToken,
      formData: signUpFormData,
    })

    if (status === 401 || errorMessage) {
      return handleErrorResponse({
        errorMessage,
        status,
        errors,
        defaultFormData,
      })
    }

    setCookie({
      name: 'accessToken',
      value: pendingAccessToken,
      options: { maxAge: ONE_DAY },
    })

    setCookie({
      name: 'refreshToken',
      value: pendingRefreshToken,
      options: { maxAge: 7 * ONE_DAY },
    })

    await clearPendingTokens()

    return {
      success: true,
      message: '회원가입에 성공했습니다.',
      formData: { name, email },
    }
  } catch (error) {
    console.log(error)
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

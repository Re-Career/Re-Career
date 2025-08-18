'use server'

import { putUser, putProfileImage } from '@/services/user'
import { getTokens } from '../auth/action'
import z from 'zod'
import { PutUserPayload } from '@/types/user'

interface ProfileFormData {
  name: string
  email: string
  mentorPosition?: string
  mentorDescription?: string
  profileImageFile?: File | null
}

interface FormState {
  success: boolean
  message: string
  formData: ProfileFormData
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
  defaultFormData: ProfileFormData
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

export const updateProfileAction = async (
  _: FormState,
  formData: FormData
): Promise<FormState> => {
  const name = formData.get('name') as string
  const email = formData.get('email') as string
  const mentorPosition = formData.get('mentorPosition') as string
  const mentorDescription = formData.get('mentorDescription') as string
  const profileImageFile = formData.get('profileImageFile') as File | null

  const schema = z.object({
    name: z.string().min(1, '이름을 입력해주세요.'),
    email: z.email('올바른 이메일 형식을 입력해주세요.'),
    mentorPosition: z.string().nullable().optional(),
    mentorDescription: z.string().nullable().optional(),
    profileImageFile: z.file().nullable().optional(),
  })

  const defaultFormData = {
    name,
    email,
    mentorPosition,
    mentorDescription,
    profileImageFile,
  }

  try {
    const { accessToken } = await getTokens()

    if (!accessToken) {
      return handleErrorResponse({
        errorMessage: '로그인이 필요합니다.',
        status: 401,
        defaultFormData,
      })
    }

    const requestData: ProfileFormData = {
      name,
      email,
      mentorPosition,
      mentorDescription,
      profileImageFile,
    }

    const parseResult = schema.safeParse(requestData)

    if (!parseResult.success) {
      throw new z.ZodError(parseResult.error.issues)
    }

    // 프로필 이미지 업로드 (파일이 있는 경우)
    if (profileImageFile && profileImageFile.size > 0) {
      const {
        errorMessage: imageErrorMessage,
        errors: imageErrors,
        status: imageStatus,
      } = await putProfileImage({
        accessToken,
        imageFile: profileImageFile,
      })

      if (imageErrorMessage) {
        return handleErrorResponse({
          errorMessage: imageErrorMessage,
          status: imageStatus,
          errors: imageErrors,
          defaultFormData,
        })
      }
    }

    // 사용자 정보 업데이트
    const userData: PutUserPayload = { name, email }
    if (mentorPosition) userData.mentorPosition = mentorPosition
    if (mentorDescription) userData.mentorDescription = mentorDescription

    const { errorMessage, errors, status } = await putUser({
      accessToken,
      userData,
    })

    if (status === 401 || errorMessage) {
      return handleErrorResponse({
        errorMessage,
        status,
        errors,
        defaultFormData,
      })
    }

    return {
      success: true,
      message: '프로필이 성공적으로 업데이트되었습니다.',
      formData: defaultFormData,
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
        formData: defaultFormData,
      }
    }

    return {
      success: false,
      message: '프로필 업데이트에 실패했습니다. 다시 시도해주세요.',
      formData: defaultFormData,
    }
  }
}

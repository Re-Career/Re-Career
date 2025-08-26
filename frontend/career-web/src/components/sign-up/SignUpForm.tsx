'use client'

import { useState, useActionState, useEffect, useCallback } from 'react'
import { useRouter } from 'next/navigation'
import TagList from './TagList'
import ProfileImageUpload from './ProfileImageUpload'
import RegionSelector from './RegionSelector'
import { RoleType } from '@/types/global'
import { PersonalityTag } from '@/types/personality-tags'
import { signUpAction } from '@/app/actions/sign-up/action'
import { isWebView, sendAuthTokensToNative } from '@/utils/webview'
import { getTokens } from '@/app/actions/auth/action'
import { BsExclamationCircle } from 'react-icons/bs'
import { deleteCookie, getCookie } from '@/app/actions/global/action'
import { ROLE_TYPES } from '@/lib/constants/global'
import { City, Province } from '@/types/location'

interface SignUpFormProps {
  role: RoleType
  tags: PersonalityTag[]
  provinces: Province[]
  cities: City[]
}

const SignUpForm = ({ role, tags, provinces, cities }: SignUpFormProps) => {
  const router = useRouter()

  const [selectedTags, setSelectedTags] = useState<number[]>([])
  const [selectedProvinceId, setSelectedProvinceId] = useState<number>(0)
  const [selectedCityId, setSelectedCityId] = useState<number>(0)
  const [errors, setErrors] = useState<Record<string, string>>({})

  const [state, formAction] = useActionState(signUpAction, {
    success: false,
    message: '',
    formData: { name: '', email: '' },
  })

  const saveNativeAuth = useCallback(async () => {
    const { accessToken } = await getTokens()

    sendAuthTokensToNative(accessToken || '', '')
  }, [])

  const checkRedirectUrl = useCallback(async () => {
    const redirectUrl = await getCookie('redirectUrl')

    if (redirectUrl) {
      await deleteCookie('redirectUrl')
      router.replace(redirectUrl)
    } else {
      router.replace('/')
    }
  }, [])

  useEffect(() => {
    const { success, status } = state

    if (status === 401) {
      router.replace('/login')
    }

    if (success) {
      if (isWebView()) {
        saveNativeAuth()
      }
      checkRedirectUrl()
    }

    if (state.errors) {
      setErrors(state.errors)
    }
  }, [state, router])

  const toggleTag = (tagId: number) => {
    const hasTag = selectedTags.includes(tagId)

    if (!hasTag && selectedTags.length >= 5) {
      alert('최대 5개까지 선택할 수 있습니다.')

      return
    }

    setSelectedTags((prev) =>
      hasTag ? prev.filter((t) => t !== tagId) : [...prev, tagId]
    )

    setErrors((prev) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { personalityTagIds, ...rest } = prev

      return rest
    })
  }

  const handleProvinceChange = (provinceId: number) => {
    setSelectedProvinceId(provinceId)
    setErrors((prev) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { provinceId: prevProvinceId, ...rest } = prev

      return rest
    })
  }

  const handleCityChange = (cityId: number) => {
    setSelectedCityId(cityId)
    setErrors((prev) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { cityId: prevCityId, ...rest } = prev

      return rest
    })
  }

  return (
    <form action={formAction}>
      <main className="flex flex-1 flex-col">
        <h1 className="px-4 py-3 text-xl leading-7 font-bold text-neutral-900">
          프로필을 완성해주세요
        </h1>
        <div className="p-4">
          <h3 className="mb-3 font-bold text-neutral-900">기본 정보</h3>
          {state.message && !state.success && (
            <div className="mb-4 flex gap-1 rounded-xl bg-red-100 p-2 text-sm text-gray-800">
              <div className="flex h-5 w-5 items-center">
                <BsExclamationCircle
                  className="h-4 w-4 text-red-500"
                  strokeWidth={0.3}
                />
              </div>
              <p className="leading-[20px]">{state.message}</p>
            </div>
          )}
          <div className="flex-1">
            <div className="flex flex-col gap-6">
              {/* Hidden inputs */}
              <input type="hidden" name="role" value={role} />
              {selectedTags.map((tagId, index) => (
                <input
                  key={index}
                  type="hidden"
                  name="personalityTagIds"
                  value={tagId.toString()}
                />
              ))}
              <input
                type="hidden"
                name="provinceId"
                value={selectedProvinceId}
              />
              <input type="hidden" name="cityId" value={selectedCityId} />

              {/* Form fields */}
              <div className="flex flex-col gap-1">
                <input
                  name="name"
                  placeholder="이름 *"
                  className="h-14 rounded-xl bg-gray-100 p-4"
                  defaultValue={state.formData.name}
                  required
                />
                {errors?.name && (
                  <span className="text-sm text-red-600">{errors.name}</span>
                )}
              </div>
              <div className="flex flex-col gap-1">
                <input
                  name="email"
                  type="email"
                  placeholder="이메일 *"
                  className="h-14 rounded-xl bg-gray-100 p-4"
                  defaultValue={state.formData.email}
                  required
                />
                {errors?.email && (
                  <span className="text-sm text-red-600">{errors.email}</span>
                )}
              </div>

              <div className="flex flex-col gap-1">
                <RegionSelector
                  onProvinceChange={handleProvinceChange}
                  onCityChange={handleCityChange}
                  cities={cities}
                  provinces={provinces}
                />
                {errors?.provinceId && (
                  <span className="text-sm text-red-600">
                    {errors.provinceId}
                  </span>
                )}
                {errors?.cityId && (
                  <span className="text-sm text-red-600">{errors.cityId}</span>
                )}
              </div>

              {/* 멘토 전용 필드 */}
              {role === ROLE_TYPES.MENTOR && (
                <>
                  <div className="flex flex-col gap-1">
                    <input
                      name="position"
                      placeholder="직책/포지션 (예: 백엔드 개발자)"
                      className="h-14 rounded-xl bg-gray-100 p-4"
                    />
                    {errors?.position && (
                      <span className="text-sm text-red-600">
                        {errors.position}
                      </span>
                    )}
                  </div>
                  <div className="flex flex-col gap-1">
                    <textarea
                      name="description"
                      placeholder="자기소개 (예: 5년차 백엔드 개발자입니다. Spring Boot와 AWS 경험이 풍부합니다.)"
                      className="min-h-20 resize-none rounded-xl bg-gray-100 p-4"
                      rows={3}
                    />
                    {errors?.description && (
                      <span className="text-sm text-red-600">
                        {errors.description}
                      </span>
                    )}
                  </div>
                </>
              )}
            </div>
          </div>
        </div>

        <ProfileImageUpload />

        <div className="p-4">
          <h3 className="mb-3 font-bold text-neutral-900">
            관심태그 (최대 5개) *
          </h3>
          <div className="flex flex-wrap gap-2">
            <TagList
              toggleTag={toggleTag}
              selectedTags={selectedTags}
              tags={tags}
            />
          </div>
          {errors?.personalityTagIds && (
            <span className="mt-2 text-sm text-red-600">
              {errors.personalityTagIds}
            </span>
          )}
        </div>
      </main>

      <footer className="sticky right-0 bottom-0 left-0 bg-white">
        <div className="flex items-center justify-center p-4">
          <button
            type="submit"
            className="bg-primary-500 h-12 w-full max-w-[480px] min-w-20 items-center justify-center overflow-hidden rounded-3xl px-5 font-bold text-neutral-900"
          >
            가입 완료
          </button>
        </div>
      </footer>
    </form>
  )
}

export default SignUpForm

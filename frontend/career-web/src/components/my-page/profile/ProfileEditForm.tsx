'use client'

import { useState, useActionState, useEffect } from 'react'
import Image from 'next/image'
import { useRouter } from 'next/navigation'
import { IoCameraOutline } from 'react-icons/io5'
import { User } from '@/types/user'
import { BsExclamationCircle } from 'react-icons/bs'
import { hasProfileImage } from '@/lib/constants/images'
import { updateProfileAction } from '@/app/actions/user/action'

interface ProfileEditFormProps {
  userData: User
}

const ProfileEditForm = ({ userData }: ProfileEditFormProps) => {
  const router = useRouter()
  const [profileImage, setProfileImage] = useState(userData.profileImageUrl)
  const [errors, setErrors] = useState<Record<string, string>>({})

  const [state, formAction] = useActionState(updateProfileAction, {
    success: false,
    message: '',
    formData: {
      name: userData.name,
      email: userData.email,
      mentorPosition: userData.mentorPosition || '',
      mentorDescription: userData.mentorDescription || '',
    },
  })

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]

    if (file) {
      const reader = new FileReader()

      reader.onload = (e) => {
        setProfileImage(e.target?.result as string)
      }

      reader.readAsDataURL(file)
    }
  }

  useEffect(() => {
    const { success, status } = state

    if (status === 401) {
      router.replace('/login')
    }

    if (success) {
      router.push('/my-page')
    }

    if (state.errors) {
      setErrors(state.errors)
    }
  }, [state, router])

  return (
    <div className="p-4">
      <form action={formAction} className="space-y-6">
        {/* 에러/성공 메시지 */}
        {state.message && (
          <div
            className={`mb-4 flex gap-1 rounded-xl p-2 text-sm ${
              state.success
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-red-800'
            }`}
          >
            {!state.success && (
              <div className="flex h-5 w-5 items-center">
                <BsExclamationCircle
                  className="h-4 w-4 text-red-500"
                  strokeWidth={0.3}
                />
              </div>
            )}
            <p className="leading-[20px]">{state.message}</p>
          </div>
        )}
        <div className="flex flex-col items-center gap-4">
          <div className="relative">
            {hasProfileImage(profileImage) ? (
              <Image
                src={profileImage!}
                alt="Profile"
                width={128}
                height={128}
                className="h-32 w-32 rounded-full object-cover"
                onError={() => setProfileImage(null)}
              />
            ) : (
              <div className="flex h-32 w-32 items-center justify-center rounded-full bg-gray-200">
                <IoCameraOutline className="h-12 w-12 text-gray-400" />
              </div>
            )}
            <label className="bg-primary-700 absolute right-0 bottom-0 cursor-pointer rounded-full p-2 text-white shadow-lg">
              <IoCameraOutline className="h-4 w-4" />
              <input
                type="file"
                name="profileImageFile"
                accept="image/*"
                onChange={handleImageChange}
                className="hidden"
              />
            </label>
          </div>
          <p className="text-sm text-gray-600">
            프로필 사진을 변경하려면 카메라 아이콘을 클릭하세요
          </p>
        </div>

        <div>
          <label
            htmlFor="name"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            이름
          </label>
          <input
            type="text"
            id="name"
            name="name"
            defaultValue={state.formData.name}
            className="focus:border-primary-500 w-full rounded-lg border border-gray-300 p-3 focus:outline-none"
            placeholder="이름을 입력하세요"
          />
          {errors?.name && (
            <span className="mt-1 text-sm text-red-600">{errors.name}</span>
          )}
        </div>

        <div>
          <label
            htmlFor="email"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            이메일
          </label>
          <input
            type="email"
            id="email"
            name="email"
            defaultValue={state.formData.email}
            className="focus:border-primary-500 w-full rounded-lg border border-gray-300 p-3 focus:outline-none"
            placeholder="이메일을 입력하세요"
          />
          {errors?.email && (
            <span className="mt-1 text-sm text-red-600">{errors.email}</span>
          )}
        </div>

        {/* 멘토 전용 필드 */}
        {userData.role === 'MENTOR' && (
          <>
            <div>
              <label
                htmlFor="mentorPosition"
                className="mb-2 block text-sm font-medium text-gray-700"
              >
                직책 / 포지션
              </label>
              <input
                type="text"
                id="mentorPosition"
                name="mentorPosition"
                defaultValue={state.formData.mentorPosition}
                className="focus:border-primary-500 w-full rounded-lg border border-gray-300 p-3 focus:outline-none"
                placeholder="예: 시니어 프론트엔드 개발자"
              />
              {errors?.mentorPosition && (
                <span className="mt-1 text-sm text-red-600">
                  {errors.mentorPosition}
                </span>
              )}
            </div>

            <div>
              <label
                htmlFor="mentorDescription"
                className="mb-2 block text-sm font-medium text-gray-700"
              >
                소개 / 설명
              </label>
              <textarea
                id="mentorDescription"
                name="mentorDescription"
                rows={4}
                defaultValue={state.formData.mentorDescription}
                className="focus:border-primary-500 w-full rounded-lg border border-gray-300 p-3 focus:outline-none"
                placeholder="멘토로서 자신을 소개해주세요..."
              />
              {errors?.mentorDescription && (
                <span className="mt-1 text-sm text-red-600">
                  {errors.mentorDescription}
                </span>
              )}
            </div>
          </>
        )}

        <div className="flex gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="flex-1 rounded-lg border border-gray-300 p-3 font-semibold text-gray-700"
          >
            취소
          </button>
          <button
            type="submit"
            className="bg-primary-500 flex-1 rounded-lg p-3 font-semibold disabled:opacity-50"
          >
            저장
          </button>
        </div>
      </form>
    </div>
  )
}

export default ProfileEditForm

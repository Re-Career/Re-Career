'use client'

import React, { useActionState, useState, useEffect } from 'react'
import TagList from './TagList'
import { RoleType } from '@/types/global'
import Image from 'next/image'
import { signUpAction } from '@/app/actions/sign-up/action'
import { PersonalityTag } from '@/types/personality-tags'
import { isWebView, sendAuthTokensToNative } from '@/utils/webview'
import { getToken } from '@/app/actions/auth/action'
import { useRouter } from 'next/router'

interface SignUpFormProps {
  role: RoleType
  tags: PersonalityTag[]
}
const SignUpForm = ({ role, tags }: SignUpFormProps) => {
  const router = useRouter()

  const [selectedTags, setSelectedTags] = useState<number[]>([])
  const [profileImagePreview, setProfileImagePreview] = useState<string | null>(
    null
  )

  const [state, formAction] = useActionState(signUpAction, {
    success: false,
    message: '',
  })

  useEffect(() => {
    const { success } = state

    const saveNativeAuth = async () => {
      const accessToken = await getToken()

      console.log(accessToken)
      sendAuthTokensToNative(accessToken, '')
    }

    if (success) {
      if (isWebView()) {
        saveNativeAuth()
      }
      router.replace('/')
    }
  }, [state])

  const toggleTag = (tagId: number) => {
    const hasTag = selectedTags.includes(tagId)

    if (!hasTag && selectedTags.length >= 5) {
      alert('최대 5개까지 선택할 수 있습니다.')

      return
    }

    setSelectedTags((prev) =>
      hasTag ? prev.filter((t) => t !== tagId) : [...prev, tagId]
    )
  }

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]

    if (file) {
      setProfileImagePreview(URL.createObjectURL(file))

      e.target.value = ''
    }
  }

  return (
    <form action={formAction} key={state.success ? 'reset' : 'keep'}>
      <main className="flex flex-1 flex-col">
        <h1 className="mb-9 px-4 py-3 text-xl leading-7 font-bold text-neutral-900">
          프로필을 완성해주세요
        </h1>

        {state.message && (
          <div
            className={`mx-4 mb-4 rounded-xl p-3 text-sm ${
              state.success
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-gray-800'
            }`}
          >
            <p>{state.message}</p>
            {state.error && (
              <p className="font-semibold text-red-800">{state.error}</p>
            )}
          </div>
        )}

        <div className="flex-1">
          <div className="flex flex-col gap-6 px-4">
            <input
              className="hidden"
              name="role"
              value={role}
              onChange={() => {}}
            />
            {selectedTags.map((tagId, index) => (
              <input
                key={index}
                type="hidden"
                name="personalityTagIds"
                value={tagId.toString()}
              />
            ))}
            <input
              name="name"
              placeholder="이름 *"
              className="h-14 rounded-xl bg-gray-100 p-4"
              required
            />
            <input
              name="email"
              type="email"
              placeholder="이메일 *"
              className="h-14 rounded-xl bg-gray-100 p-4"
              required
            />
          </div>
          <div className="p-4">
            <div className="flex justify-between">
              <h3 className="mb-3 font-bold text-neutral-900">프로필 사진</h3>

              <div className="flex gap-1">
                <label className="cursor-pointer items-center justify-center overflow-hidden rounded-2xl bg-gray-100 px-4 py-2 text-center text-sm leading-tight font-medium text-neutral-900">
                  {profileImagePreview ? '변경' : '추가'}
                  <input
                    name="profileImage"
                    type="file"
                    className="hidden"
                    accept="image/*"
                    onChange={handleImageChange}
                  />
                </label>
                {profileImagePreview && (
                  <button
                    onClick={() => setProfileImagePreview(null)}
                    className="cursor-pointer items-center justify-center overflow-hidden rounded-2xl bg-red-300 px-4 py-2 text-center text-sm leading-tight font-medium text-neutral-900"
                  >
                    삭제
                  </button>
                )}
              </div>
            </div>

            {profileImagePreview && (
              <div className="mt-1 flex justify-center">
                <Image
                  width={200}
                  height={200}
                  src={profileImagePreview}
                  alt="profile_image_preview"
                  className="rounded-lg object-cover"
                  style={{ width: 'auto', height: 'auto' }}
                />
              </div>
            )}
          </div>
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
          </div>
        </div>
      </main>
      <footer>
        <div className="flex items-center justify-center p-4">
          <button
            type="submit"
            className="bg-primary h-12 w-full max-w-[480px] min-w-20 items-center justify-center overflow-hidden rounded-3xl px-5 font-bold text-neutral-900"
          >
            가입 완료
          </button>
        </div>
      </footer>
    </form>
  )
}

export default SignUpForm

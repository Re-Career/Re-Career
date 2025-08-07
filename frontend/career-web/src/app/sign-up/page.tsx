'use client'
import Header from '@/components/common/Header'
import TagList from '@/components/sign-up/TagList'
import { sendMessageToNative, WebViewMessageTypes } from '@/utils/webview'
import React, { useState } from 'react'
import { useActionState } from 'react'

type FormState = {
  success: boolean
  message: string
  data?: { name: string; email: string; interestTags: string[] }
  error?: string
}

async function handleSignUpAction(
  _: FormState,
  formData: FormData
): Promise<FormState> {
  const name = formData.get('name') as string
  const email = formData.get('email') as string
  const profileImage = formData.get('profileImage') as File
  const interestTags = formData.getAll('interestTags') as string[]

  try {
    sendMessageToNative({ type: WebViewMessageTypes.CLOSE_WEBVIEW })

    return {
      success: true,
      message: '회원가입이 완료되었습니다.',
      data: { name, email, interestTags },
    }
  } catch (error) {
    return {
      success: false,
      message: '회원가입에 실패했습니다. 다시 시도해주세요.',
      error: error instanceof Error ? error.message : 'Unknown error',
    }
  }
}

const SignUpPage = () => {
  const [selectedTags, setSelectedTags] = useState<number[]>([])
  const [state, formAction] = useActionState(handleSignUpAction, {
    success: false,
    message: '',
  })

  const toggleTag = (tagId: number) => {
    setSelectedTags((prev) =>
      prev.includes(tagId) ? prev.filter((t) => t !== tagId) : [...prev, tagId]
    )
  }

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <form action={formAction}>
        <main className="flex flex-1 flex-col">
          <h1 className="mb-9 px-4 py-3 text-xl leading-7 font-bold text-neutral-900">
            프로필을 완성해주세요
          </h1>

          {state.message && (
            <div
              className={`mx-4 mb-4 rounded-xl p-3 text-sm ${
                state.success
                  ? 'bg-green-100 text-green-800'
                  : 'bg-red-100 text-red-800'
              }`}
            >
              {state.message}
            </div>
          )}

          <div className="flex-1">
            <div className="flex flex-col gap-6 px-4">
              <input
                name="name"
                placeholder="이름"
                className="h-14 rounded-xl bg-gray-100 p-4"
                required
              />
              <input
                name="email"
                type="email"
                placeholder="이메일"
                className="h-14 rounded-xl bg-gray-100 p-4"
                required
              />
            </div>
            <div className="flex items-center justify-between p-4">
              <h3 className="align-middle font-bold text-neutral-900">
                프로필 사진
              </h3>
              <label className="h-8 w-21 items-center justify-center overflow-hidden rounded-2xl bg-gray-100 px-4 py-2 text-center text-sm leading-tight font-medium text-neutral-900">
                추가
                <input
                  name="profileImage"
                  type="file"
                  className="hidden"
                  accept="image/*"
                />
              </label>
            </div>
            <div className="p-4">
              <h3 className="mb-3 font-bold text-neutral-900">관심태그</h3>
              <div className="flex flex-wrap gap-2">
                <TagList toggleTag={toggleTag} selectedTags={selectedTags} />
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
    </>
  )
}

export default SignUpPage

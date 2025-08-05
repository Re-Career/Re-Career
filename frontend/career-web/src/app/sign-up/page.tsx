'use client'
import Header from '@/components/common/Header'
import { sendMessageToNative, WebViewMessageTypes } from '@/utils/webview'
import React, { useState } from 'react'

const INTEREST_TAGS = [
  '스타트업',
  '인간관계',
  '리더십',
  '일과 생활 관리',
  '자기성장',
]

const SignUpPage = () => {
  const [selectedTags, setSelectedTags] = useState<string[]>([])

  const toggleTag = (tag: string) => {
    setSelectedTags((prev) =>
      prev.includes(tag) ? prev.filter((t) => t !== tag) : [...prev, tag]
    )
  }

  const handleSignUp = () => {
    sendMessageToNative({ type: WebViewMessageTypes.CLOSE_WEBVIEW })
  }

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <main className="flex flex-1 flex-col">
        <h1 className="mb-9 px-4 py-3 text-xl leading-7 font-bold text-neutral-900">
          프로필을 완성해주세요
        </h1>
        <div className="flex-1">
          <div className="flex flex-col gap-6 px-4">
            <input
              placeholder="이름"
              className="h-14 rounded-xl bg-gray-100 p-4"
            />
            <input
              placeholder="이메일"
              className="h-14 rounded-xl bg-gray-100 p-4"
            />
          </div>
          <div className="flex items-center justify-between p-4">
            <h3 className="align-middle font-bold text-neutral-900">
              프로필 사진
            </h3>
            <label className="h-8 w-21 items-center justify-center overflow-hidden rounded-2xl bg-gray-100 px-4 py-2 text-center text-sm leading-tight font-medium text-neutral-900">
              추가
              <input type="file" className="hidden" />
            </label>
          </div>
          <div className="p-4">
            <h3 className="mb-3 font-bold text-neutral-900">관심태그</h3>
            <div className="flex flex-wrap gap-2">
              {INTEREST_TAGS.map((tag) => (
                <button
                  key={tag}
                  onClick={() => toggleTag(tag)}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                    selectedTags.includes(tag)
                      ? 'bg-blue-500 text-white'
                      : 'bg-gray-100 text-neutral-900 hover:bg-gray-200'
                  }`}
                >
                  {tag}
                </button>
              ))}
            </div>
          </div>
        </div>
      </main>
      <footer>
        <div className="flex items-center justify-center p-4">
          <button
            className="bg-primary h-12 w-full max-w-[480px] min-w-20 items-center justify-center overflow-hidden rounded-3xl px-5 font-bold text-neutral-900"
            onClick={handleSignUp}
          >
            가입 완료
          </button>
        </div>
      </footer>
    </>
  )
}

export default SignUpPage

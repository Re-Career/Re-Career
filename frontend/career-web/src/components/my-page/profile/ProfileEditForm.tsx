'use client'

import { useState } from 'react'
import Image from 'next/image'
import { useRouter } from 'next/navigation'
import { IoCameraOutline } from 'react-icons/io5'
import { User } from '@/types/user'

interface ProfileEditFormProps {
  userData: User
}

const ProfileEditForm = ({ userData }: ProfileEditFormProps) => {
  const router = useRouter()
  const [name, setName] = useState(userData.name)
  const [profileImage, setProfileImage] = useState(userData.profileImageUrl)
  const [isLoading, setIsLoading] = useState(false)

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // TODO: API 호출로 프로필 업데이트
      console.log('Profile update:', { name, profileImage })

      // 성공 시 마이페이지로 이동
      router.push('/my-page')
    } catch (error) {
      console.error('Profile update failed:', error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="p-4">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="flex flex-col items-center gap-4">
          <div className="relative">
            <Image
              src={profileImage ?? ''}
              alt="Profile"
              width={128}
              height={128}
              className="rounded-full object-cover"
            />
            <label className="absolute right-0 bottom-0 cursor-pointer rounded-full bg-blue-500 p-2 text-white shadow-lg">
              <IoCameraOutline className="h-4 w-4" />
              <input
                type="file"
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
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full rounded-lg border border-gray-300 p-3 focus:border-blue-500 focus:outline-none"
            placeholder="이름을 입력하세요"
          />
        </div>

        <div className="flex gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="flex-1 rounded-lg border border-gray-300 p-3 text-gray-700"
          >
            취소
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="flex-1 rounded-lg bg-blue-500 p-3 text-white disabled:opacity-50"
          >
            {isLoading ? '저장 중...' : '저장'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default ProfileEditForm

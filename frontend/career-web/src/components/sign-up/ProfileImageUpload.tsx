import React, { useState } from 'react'
import Image from 'next/image'

const ProfileImageUpload = () => {
  const [profileImagePreview, setProfileImagePreview] = useState<string | null>(null)

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]

    if (file) {
      setProfileImagePreview(URL.createObjectURL(file))
      e.target.value = ''
    }
  }

  const handleImageRemove = () => {
    setProfileImagePreview(null)
  }

  return (
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
              type="button"
              onClick={handleImageRemove}
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
  )
}

export default ProfileImageUpload
'use client'

import { deleteProfileImageAction } from '@/app/actions/user/action'
import { BottomSheet } from '@/components/common'
import { hasProfileImage } from '@/lib/constants/images'
import { User } from '@/types/user'
import Image from 'next/image'
import { useRouter } from 'next/navigation'
import { useState } from 'react'
import { IoCameraOutline as CameraIcon } from 'react-icons/io5'
import { LuPencil as PencilIcon } from 'react-icons/lu'

type ProfileImageProps = {
  user: User
}

const ProfileImage = ({ user }: ProfileImageProps) => {
  const [isBottomSheetOpen, setBottomSheetOpen] = useState(false)
  const router = useRouter()

  const handleDelete = async () => {
    setBottomSheetOpen(false)

    await deleteProfileImageAction()
  }

  const handleEdit = () => {
    setBottomSheetOpen(false)
    router.push('/settings/profile')
  }

  const handleProfileImageBottomSheet = () => {
    setBottomSheetOpen(true)
  }

  const menuItems = [
    { label: '프로필 사진 수정', onClick: handleEdit },
    { label: '프로필 사진 삭제', onClick: handleDelete, isDestructive: true },
  ]

  return (
    <>
      <div className="relative">
        <div
          className="relative h-32 w-32 cursor-pointer"
          onClick={handleProfileImageBottomSheet}
        >
          {hasProfileImage(user.profileImageUrl) ? (
            <Image
              src={user.profileImageUrl!}
              alt={`my_page_image_${user.id}`}
              fill
              className="rounded-full object-cover"
            />
          ) : (
            <div className="flex h-32 w-32 items-center justify-center rounded-full bg-gray-200">
              <CameraIcon className="h-12 w-12 text-gray-400" />
            </div>
          )}
          <div className="absolute right-0 bottom-0 rounded-full bg-white p-2 shadow-lg">
            <PencilIcon className="h-4 w-4 text-gray-600" />
          </div>
        </div>
      </div>
      <BottomSheet
        isOpen={isBottomSheetOpen}
        onClose={() => setBottomSheetOpen(false)}
        menuItems={menuItems}
      />
    </>
  )
}

export default ProfileImage

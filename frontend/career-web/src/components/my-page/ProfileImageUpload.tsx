'use client'

import { useRef } from 'react'

interface ProfileImageUploadProps {
  onImageChange: (file: File) => void
  className?: string
  children?: React.ReactNode
}

export const ProfileImageUpload = ({ 
  onImageChange, 
  className, 
  children 
}: ProfileImageUploadProps) => {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]

    if (file) {
      onImageChange(file)
    }
  }

  return (
    <>
      <button onClick={handleClick} className={className}>
        {children}
      </button>
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        onChange={handleFileChange}
        className="hidden"
      />
    </>
  )
}
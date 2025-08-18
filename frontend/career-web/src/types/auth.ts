export interface DefaultSignUpFormData {
  name: string
  email: string
  role: string
  provinceId: number
  cityId?: number
  personalityTagIds: number[]
  position?: string // mentor only
  description?: string // mentor only
}

export interface SignUpFormData extends DefaultSignUpFormData {
  profileImageFile: File
}

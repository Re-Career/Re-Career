export interface DefaultSignUpFormData {
  name: string
  email: string
  role: string
  region: string
  personalityTagIds: string[]
}

export interface SignUpFormData extends DefaultSignUpFormData {
  profileImageFile: File
}

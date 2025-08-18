// 프로필 이미지가 있는지 확인
export const hasProfileImage = (profileImageUrl?: string | null): boolean => {
  return !!(profileImageUrl && profileImageUrl.trim() !== '')
}
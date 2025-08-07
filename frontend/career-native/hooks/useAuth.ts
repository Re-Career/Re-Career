import AsyncStorage from '@react-native-async-storage/async-storage'

export const useAuth = () => {
  // 토큰 저장
  const saveTokens = async (accessToken: string, refreshToken: string) => {
    try {
      const tokenData = {
        accessToken,
        refreshToken,
        savedAt: new Date().toISOString(),
      }

      await AsyncStorage.setItem('authTokens', JSON.stringify(tokenData))
    } catch (error) {
      console.error('토큰 저장 오류:', error)
    }
  }

  // 토큰 불러오기
  const loadTokens = async () => {
    try {
      const storedTokens = await AsyncStorage.getItem('authTokens')

      if (storedTokens) {
        const tokenData = JSON.parse(storedTokens)

        return tokenData
      }
    } catch (error) {
      console.error('토큰 불러오기 오류:', error)
    }
  }

  // 토큰 삭제 (로그아웃)
  const clearTokens = async () => {
    try {
      await AsyncStorage.removeItem('authTokens')
    } catch (error) {
      console.error('토큰 삭제 오류:', error)
    }
  }

  return {
    saveTokens,
    clearTokens,
    loadTokens,
  }
}

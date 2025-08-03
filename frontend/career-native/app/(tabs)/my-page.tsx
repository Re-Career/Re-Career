import { ThemedText } from '@/components/ThemedText'
import { ThemedView } from '@/components/ThemedView'

export default function MyPageScreen() {
  return (
    <ThemedView className="flex-1">
      <ThemedText type="title">MyPage Screen</ThemedText>
    </ThemedView>
  )
}

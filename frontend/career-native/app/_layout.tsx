import {
  DarkTheme,
  DefaultTheme,
  ThemeProvider,
} from '@react-navigation/native'
import { useFonts } from 'expo-font'
import { Stack } from 'expo-router'
import { StatusBar } from 'expo-status-bar'
import 'react-native-reanimated'
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context'
import '../global.css'

import { useColorScheme } from '@/hooks/useColorScheme'

const RootLayout = () => {
  const [loaded] = useFonts({
    'SpaceMono-Regular': require('../assets/fonts/SpaceMono-Regular.ttf'),
  })
  const colorScheme = useColorScheme()

  if (!loaded) {
    return null
  }

  return (
    <SafeAreaProvider>
      <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
        <SafeAreaView className="flex-1" edges={['top']}>
          <Stack
            screenOptions={{
              headerShown: false,
            }}
          >
            <Stack.Screen name="(tabs)" />
            <Stack.Screen
              name="(modal)/login"
              options={{
                presentation: 'modal',
              }}
            />
            <Stack.Screen name="+not-found" />
          </Stack>
        </SafeAreaView>
        <StatusBar style="auto" />
      </ThemeProvider>
    </SafeAreaProvider>
  )
}

export default RootLayout

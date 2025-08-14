import { StatusBar } from 'expo-status-bar'
import React from 'react'
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context'
import '../global.css'

import { AppWebView } from '@/components'

const RootLayout = () => {
  return (
    <SafeAreaProvider>
      <SafeAreaView className="flex-1" edges={['top']}>
        <AppWebView />
        <StatusBar style="auto" />
      </SafeAreaView>
    </SafeAreaProvider>
  )
}

export default RootLayout

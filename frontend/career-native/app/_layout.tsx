import React from 'react'
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context'
import '../global.css'

import { AppWebView } from '@/components/common'

const RootLayout = () => {
  return (
    <SafeAreaProvider>
      <SafeAreaView className="flex-1" edges={['top']}>
        <AppWebView />
      </SafeAreaView>
    </SafeAreaProvider>
  )
}

export default RootLayout

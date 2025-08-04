import { CareerWebView } from '../../components'
import { useRouter } from 'expo-router'
import React from 'react'

export default function HomeScreen() {
  const router = useRouter()

  const handleMessage = (event: any) => {
    try {
      const data = JSON.parse(event.nativeEvent.data)

      if (data.action === 'login') {
        router.push('/(modal)/login')
      }
    } catch (error) {
      console.log('Message parsing error:', error)
    }
  }

  return <CareerWebView onMessage={handleMessage} />
}

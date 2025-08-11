import React from 'react'
import { useLocalSearchParams } from 'expo-router'
import { AppWebView } from '@/components'

const MentorProfileScreen = () => {
  const { id } = useLocalSearchParams()

  return <AppWebView path={`/profile/mentor/${id}`} />
}

export default MentorProfileScreen

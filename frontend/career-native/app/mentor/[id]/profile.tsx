import React from 'react'
import { useLocalSearchParams } from 'expo-router'
import { AppWebView } from '@/components'

const MentorProfileScreen = () => {
  const { id } = useLocalSearchParams()

  return <AppWebView path={`mentor/${id}/profile`} />
}

export default MentorProfileScreen

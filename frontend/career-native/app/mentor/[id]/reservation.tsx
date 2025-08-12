import React from 'react'
import { useLocalSearchParams } from 'expo-router'
import { AppWebView } from '@/components'

const MentorReservationScreen = () => {
  const { id } = useLocalSearchParams()

  return <AppWebView path={`mentor/${id}/reservation`} />
}

export default MentorReservationScreen

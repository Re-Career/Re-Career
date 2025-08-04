import { CareerWebView } from '@/components/common'
import React from 'react'

const Login = () => {
  return (
    <CareerWebView
      path="login"
      javaScriptEnabled={true}
      domStorageEnabled={true}
    />
  )
}

export default Login

'use client'

import { useEffect } from 'react'

export default function SaveAuthInfo({
  sendAuthTokensToNative,
}: {
  sendAuthTokensToNative: () => void
}) {
  useEffect(() => {
    sendAuthTokensToNative()
  }, [])

  return <></>
}

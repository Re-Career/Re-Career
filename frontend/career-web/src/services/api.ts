import axios from 'axios'
import { FetchResponse } from '@/types/global'
import { refreshAccessToken } from './server/auth'

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const fetchUrl = async <T>(
  url: string,
  init?: RequestInit,
  retry = true
): Promise<FetchResponse<T>> => {
  const { headers, body, ...rest } = init || {}

  const isFormData = body instanceof FormData

  const finalHeaders: HeadersInit = {
    ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
    ...headers,
  }

  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}${url}`, {
    headers: finalHeaders,
    body,
    ...rest,
  })

  // 401 에러 시 토큰 갱신 후 재시도
  if (res.status === 401 && retry) {
    const refreshResult = await refreshAccessToken()

    if (refreshResult.data) {
      // 새 토큰으로 헤더 업데이트
      const newHeaders: HeadersInit = {
        ...finalHeaders,
        Authorization: `Bearer ${refreshResult.data}`,
      }

      // 재시도 (retry=false로 무한루프 방지)
      return fetchUrl(
        url,
        {
          ...init,
          headers: newHeaders,
        },
        false
      )
    }
  }

  const isSuccess = res.ok
  const data = await res.json().catch(() => {})

  let errorMessage: string = ''
  let errors = {}

  if (!isSuccess) {
    errors = data?.errors
    errorMessage = data?.message || '요청을 처리할 수 없습니다.'
  }

  return { errorMessage, data: data.data, errors, status: res.status }
}

import axios from 'axios'
import { FetchResponse } from '@/types/global'

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const fetchUrl = async <T>(
  url: string,
  init?: RequestInit
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

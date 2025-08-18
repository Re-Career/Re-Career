import axios from 'axios'

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const fetchUrl = async (
  url: string,
  init?: RequestInit
): Promise<Response> => {
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

  return res
}

import { getToken } from '@/app/actions/auth/action'
import { fetchUrl } from './api'
import { User } from '@/types/user'

export const getUserProfile = async (): Promise<User> => {
  const token = await getToken()

  const { data } = await fetchUrl('/user/profile', {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  })

  return data
}

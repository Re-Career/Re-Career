import { FilterConfig, Mentor, MentorDetail } from '@/types/mentor'
import { fetchUrl } from '../api'
import { ONE_DAY } from '@/lib/constants/global'

export const getMentor = async (id: string): Promise<MentorDetail> => {
  const res = await fetchUrl(`/mentors/${id}`, {
    next: { revalidate: ONE_DAY },
  })

  const { data } = await res.json()

  return data
}

export const getFilterOptions = async (): Promise<FilterConfig[]> => {
  const res = await fetchUrl(`/mentors/filters`, {
    next: { revalidate: ONE_DAY },
  })

  const { data } = await res.json()

  return data
}

export const getMentors = async (region?: string): Promise<Mentor[]> => {
  const res = await fetchUrl(`/mentors?region=${region || '서울'}`, {
    next: { revalidate: ONE_DAY },
  })

  const { data } = await res.json()

  return data
}

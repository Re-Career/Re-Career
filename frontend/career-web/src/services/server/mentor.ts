import { FilterConfig, Mentor, MentorDetail } from '@/types/mentor'
import { fetchUrl } from '../api'
import { ONE_DAY } from '@/lib/constants/global'
import { FetchResponse } from '@/types/global'

export const getMentor = async (id: string): Promise<FetchResponse<MentorDetail>> => {
  return await fetchUrl<MentorDetail>(`/mentors/${id}`, {
    next: { revalidate: ONE_DAY },
  })
}

export const getFilterOptions = async (): Promise<FetchResponse<FilterConfig[]>> => {
  return await fetchUrl<FilterConfig[]>(`/mentors/filters`, {
    next: { revalidate: ONE_DAY },
  })
}

export const getMentors = async (region?: string): Promise<FetchResponse<Mentor[]>> => {
  return await fetchUrl<Mentor[]>(`/mentors?region=${region || '서울'}`, {
    next: { revalidate: ONE_DAY },
  })
}

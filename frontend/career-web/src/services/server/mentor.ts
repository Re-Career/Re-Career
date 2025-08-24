import { FilterConfig, Mentor, MentorDetail } from '@/types/mentor'
import { fetchUrl } from '../api'
import { ONE_DAY } from '@/lib/constants/global'
import { filterOptions } from '@/mocks/matching/filterOptions'

export const getMentor = async (id: string): Promise<MentorDetail> => {
  const res = await fetchUrl(`/mentors/${id}`, {
    next: { revalidate: ONE_DAY },
  })

  const { data } = await res.json()

  return data
}

export const getFilterOptions = (): FilterConfig[] => {
  // 임시로 mock 데이터 사용 (실제 API 데이터 문제로 인해)
  return filterOptions
}

export const getMentors = async (region?: string): Promise<Mentor[]> => {
  const res = await fetchUrl(`/mentors?region=${region || '서울'}`, {
    next: { revalidate: ONE_DAY },
  })

  const { data } = await res.json()

  return data
}

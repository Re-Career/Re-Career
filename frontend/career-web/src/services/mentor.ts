import {
  FilterConfig,
  FilterOptions,
  Mentor,
  MentorDetail,
} from '@/types/mentor'
import { fetchUrl, api } from './api'
import { ONE_DAY } from '@/lib/constants/global'
import { filterOptions } from '@/mocks/matching/filterOptions'

export const getMentor = async (id: string): Promise<MentorDetail> => {
  const res = await fetchUrl(`/mentors/${id}`)

  const { data } = await res.json()

  return data
}

export const getFilteredMentors = async ({
  mentorName,
  filters,
}: {
  mentorName: string
  filters: FilterOptions
}): Promise<{ primary: Mentor[]; secondary: Mentor[] }> => {
  const params = new URLSearchParams()

  if (mentorName) {
    params.append('keyword', mentorName)
  }

  Object.entries(filters).forEach(([key, values]) => {
    if (values && values.length > 0) {
      params.set(key, values.join(','))
    }
  })

  const {
    data: { data },
  } = await api.get(`/mentors/search?${params.toString()}`)

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

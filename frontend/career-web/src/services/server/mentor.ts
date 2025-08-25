import {
  FilterConfig,
  Mentor,
  MentorDetail,
  FilterOptions,
} from '@/types/mentor'
import { fetchUrl } from '../api'
import { ONE_DAY } from '@/lib/constants/global'
import { FetchResponse } from '@/types/global'

interface FilteredMentors {
  recommendedList: Mentor[]
  searchedList: Mentor[]
}

export const getMentor = async (
  id: string
): Promise<FetchResponse<MentorDetail>> => {
  return await fetchUrl<MentorDetail>(`/mentors/${id}`, {
    next: { revalidate: ONE_DAY, tags: ['mentors', `mentor-${id}`] },
  })
}

export const getFilterOptions = async (): Promise<
  FetchResponse<FilterConfig[]>
> => {
  return await fetchUrl<FilterConfig[]>(`/mentors/filters`, {
    next: { revalidate: ONE_DAY, tags: ['mentors', 'filters'] },
  })
}

export const getMentors = async (
  region?: string
): Promise<FetchResponse<Mentor[]>> => {
  return await fetchUrl<Mentor[]>(`/mentors?region=${region || '서울'}`, {
    next: { revalidate: ONE_DAY },
  })
}

export const getFilteredMentors = async ({
  mentorName,
  filters,
}: {
  mentorName: string
  filters: FilterOptions
}): Promise<FetchResponse<FilteredMentors>> => {
  const params = new URLSearchParams()

  if (mentorName) {
    params.append('keyword', mentorName)
  }

  Object.entries(filters).forEach(([key, values]) => {
    if (values && values.length > 0) {
      params.set(key, values.join(','))
    }
  })

  return await fetchUrl<FilteredMentors>(
    `/mentors/search?${params.toString()}`,
    {
      next: { revalidate: ONE_DAY, tags: ['mentors', 'mentor-search'] },
    }
  )
}

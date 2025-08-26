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
  provinceId?: number
): Promise<FetchResponse<Mentor[]>> => {
  return await fetchUrl<Mentor[]>(`/mentors?provinceId=${provinceId || 1}}`, {
    next: {
      revalidate: ONE_DAY,
      tags: ['mentors', 'mentor-list', `mentor-list-province-id-${provinceId}`],
    },
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

  const _params = params.toString()

  return await fetchUrl<FilteredMentors>(`/mentors/search?${_params}`, {
    next: {
      revalidate: ONE_DAY,
      tags: ['mentors', 'mentor-search', `mentor-search-${_params}`],
    },
  })
}

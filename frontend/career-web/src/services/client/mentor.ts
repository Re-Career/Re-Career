import { FilterOptions, Mentor } from '@/types/mentor'
import { api } from '../api'

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

import { FilterOptions, Mentor } from '@/types/mentor'
import { fetchUrl } from './api'
import { searchMentors } from '@/mocks/home/mentors-search'

export const getMentor = async (id: string): Promise<Mentor> => {
  const { data } = await fetchUrl(`/mentors/${id}`)

  return data
}

export const getFilteredMenters = ({
  mentorName,
  filters,
}: {
  mentorName: string
  filters: FilterOptions
}) => {
  const filteredMentors = searchMentors.filter((mentor) => {
    // 이름 필터링
    const nameMatch = mentor.name.toLowerCase().includes(mentorName.toLowerCase())
    
    // 모든 필터 조건 확인
    const filterMatch = Object.entries(filters).every(([filterKey, filterValues]) => {
      if (!Array.isArray(filterValues) || filterValues.length === 0) {
        return true // 필터가 비어있으면 통과
      }
      
      const mentorValue = mentor[filterKey as keyof typeof mentor]
      
      if (typeof mentorValue === 'string') {
        return filterValues.some(value => {
          // meetingType의 경우 'both'는 모든 조건과 매치
          if (filterKey === 'meetingType' && mentorValue === 'both') {
            return true
          }
          return mentorValue.includes(value)
        })
      }
      
      return false
    })
    
    return nameMatch && filterMatch
  })

  return filteredMentors
}

export const getRecommenedMentors = () => {
  const recommendedMentors = searchMentors.slice(0, 3)

  return recommendedMentors
}

import { PersonalityTag } from '@/types/personality-tags'
import { api } from './api'

export const personalityUrls = {
  tags: () => '/personality-tags' as const,
}

export const getPersonalityTags = async (): Promise<PersonalityTag[]> => {
  try {
    console.log('API Base URL:', process.env.NEXT_PUBLIC_API_URL)
    console.log('Fetching personality tags from:', personalityUrls.tags())
    
    const {
      data: { data },
    } = await api.get(personalityUrls.tags())

    console.log('Personality tags response:', data)
    return data
  } catch (error) {
    console.error('Failed to fetch personality tags:', error)
    throw error
  }
}

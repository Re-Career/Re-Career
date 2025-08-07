import { PersonalityTag } from '@/types/personality-tags'
import { api } from './api'

export const personalityUrls = {
  tags: () => '/personality-tags' as const,
}
export const getPersonalityTags = async (): Promise<PersonalityTag[]> => {
  const { data } = await api.get(personalityUrls.tags())
  return data
}

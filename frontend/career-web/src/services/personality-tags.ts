import { PersonalityTag } from '@/types/personality-tags'
import { fetchUrl } from './api'

export const getPersonalityTags = async (): Promise<PersonalityTag[]> => {
  const { data } = await fetchUrl('/personality-tags', { cache: 'force-cache' })

  return data
}

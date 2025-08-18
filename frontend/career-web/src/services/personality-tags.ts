import { PersonalityTag } from '@/types/personality-tags'
import { fetchUrl } from './api'

export const getPersonalityTags = async (): Promise<PersonalityTag[]> => {
  const res = await fetchUrl('/personality-tags', { cache: 'force-cache' })
  const { data } = await res.json()

  return data
}

import { PersonalityTag } from '@/types/personality-tags'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'

export const getPersonalityTags = async (): Promise<FetchResponse<PersonalityTag[]>> => {
  return await fetchUrl<PersonalityTag[]>('/personality-tags', { cache: 'force-cache' })
}

import { fetchUrl } from '../api'
import { News } from '@/types/news'
import { FetchResponse } from '@/types/global'

export const getNews = async (): Promise<FetchResponse<News[]>> => {
  return await fetchUrl<News[]>('/news', {
    next: { revalidate: 60 * 60, tags: ['news'] },
  })
}

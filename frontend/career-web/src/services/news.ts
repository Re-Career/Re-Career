import { fetchUrl } from './api'
import { News } from '@/types/news'


export const getNews = async (): Promise<News[]> => {
  const res = await fetchUrl('/news', {
    next: { revalidate: 60 * 60 },
  })
  const { data } = await res.json()

  return data
}

import { fetchUrl } from './api'
import { News } from '@/types/news'


export const getNews = async (): Promise<News[]> => {
  const { data } = await fetchUrl('/news', {
    next: { revalidate: 60 * 60 },
  })

  return data
}

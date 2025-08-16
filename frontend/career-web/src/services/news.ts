import { fetchUrl } from './api'

const url = `${process.env.NEXT_PUBLIC_API_URL}/news`

export const getNews = async (): Promise<News[]> => {
  const { data } = await fetchUrl('/news', {
    next: { revalidate: 60 * 60 },
  })
  return data
}

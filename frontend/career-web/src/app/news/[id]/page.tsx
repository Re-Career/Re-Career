import { Header, PageWithHeader } from '@/components/layout'
import { getNews } from '@/services/server/news'
import Image from 'next/image'
import { notFound } from 'next/navigation'

const NewsDetailPage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params
  const { data: newsList } = await getNews()

  const news = newsList.find((item) => item.id === parseInt(id))

  if (!news) {
    return notFound()
  }

  return (
    <>
      <Header showBackButton title="뉴스" />
      <PageWithHeader>
        <article className="px-4 py-6">
          <div className="space-y-4">
            <div className="space-y-2">
              <span className="inline-block rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-800">
                {news.category}
              </span>
              <h1 className="text-2xl leading-tight font-bold text-neutral-900">
                {news.title}
              </h1>
            </div>

            <div className="flex w-full justify-center">
              <Image
                src={news.imageUrl}
                alt={`news_image_${news.title}`}
                width={400}
                height={300}
                priority
              />
            </div>

            <div className="prose prose-gray max-w-none">
              <p className="leading-relaxed whitespace-pre-line text-gray-700">
                {news.description}
              </p>
            </div>

            {news.url && (
              <div className="border-t border-gray-200 pt-4">
                <a
                  href={news.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center font-medium text-blue-600 hover:text-blue-800"
                >
                  원문 보기
                  <svg
                    className="ml-1 h-4 w-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"
                    />
                  </svg>
                </a>
              </div>
            )}
          </div>
        </article>
      </PageWithHeader>
    </>
  )
}

export default NewsDetailPage

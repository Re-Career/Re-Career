import React from 'react'
import Image from 'next/image'
import { getNews } from '@/services/server/news'

const IndustryNews = async () => {
  const news = await getNews()

  return (
    <section>
      <h2 className="section-title">최신 산업뉴스</h2>

      <div className="scrollbar-hide-x flex flex-col gap-4 px-4">
        {news.map((news) => (
          <div key={news.id} className="rounded-lg">
            <div className="flex gap-4">
              <div className="flex flex-1 flex-col gap-1">
                <span className="font-medium text-gray-600">
                  {news.category}
                </span>

                <h3 className="line-clamp-2 font-bold text-neutral-900">
                  {news.title}
                </h3>

                <p className="line-clamp-2 text-sm text-gray-600">
                  {news.description}
                </p>
              </div>
              <div className="flex-shrink-0">
                <Image
                  src={news.imageUrl}
                  alt={news.title}
                  width={120}
                  height={120}
                  className="rounded-lg object-cover"
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}

export default IndustryNews

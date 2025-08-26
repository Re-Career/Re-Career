import React from 'react'
import { getNews } from '@/services/server/news'
import { FixedSizeImage } from '../common'

const IndustryNews = async () => {
  const { data: news } = await getNews()

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
              <FixedSizeImage
                src={news.imageUrl}
                alt={`new_image_${news.title}`}
                isCircle={false}
              />
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}

export default IndustryNews

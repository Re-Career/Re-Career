import React from 'react'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import { getTrendPositions } from '@/services/server/positions'
import { FixedSizeImage } from '../common'

const TrendpositionList = async () => {
  const { data: trendPositions } = await getTrendPositions()

  return (
    <section>
      <h2 className="section-title">최신 트렌드 TOP 20 직업</h2>

      <HorizontalScroll>
        {trendPositions.map((position) => (
          <Link
            key={position.id}
            className="flex flex-col gap-3"
            href={`/position-detail/${position.id}`}
          >
            <FixedSizeImage
              src={position.imageUrl}
              alt={`position_trend_20_${position.name}`}
              size="lg"
              isCircle={false}
            />
            <div>
              <h3 className="font-medium text-neutral-900">{position.name}</h3>
              <span className="text-sm text-gray-600">{position.category}</span>
            </div>
          </Link>
        ))}
      </HorizontalScroll>
    </section>
  )
}

export default TrendpositionList

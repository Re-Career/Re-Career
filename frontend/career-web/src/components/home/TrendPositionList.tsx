import React from 'react'
import Image from 'next/image'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import { getTrendPositions } from '@/services/positions'

const TrendpositionList = async () => {
  const trendPositions = await getTrendPositions()

  return (
    <section>
      <h2 className="section-title">최신 트렌드 TOP 20 직업</h2>

      <HorizontalScroll>
        {trendPositions.map((position) => (
          <Link
            key={position.id}
            className="flex flex-col gap-3"
            href={`/position-description/${position.id}`}
          >
            <div className="relative h-40 w-40 overflow-hidden rounded-xl">
              <Image
                src={position.imageUrl}
                alt={`position_trend_20_${position.name}`}
                fill
                className="object-cover"
              />
            </div>

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

import React from 'react'
import Image from 'next/image'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import { getTrendPositions } from '@/services/positions'

const TrendPositionList = async () => {
  const trendPositions = await getTrendPositions()

  return (
    <section>
      <h2 className="section-title">최신 트렌드 TOP 20 직업</h2>

      <HorizontalScroll>
        {trendPositions.map((Position) => (
          <Link
            key={Position.id}
            className="flex flex-col gap-3"
            href={`/Position-description/${Position.id}`}
          >
            <div className="relative h-40 w-40 overflow-hidden rounded-xl">
              <Image
                src={Position.imageUrl}
                alt={`Position_trend_20_${Position.name}`}
                fill
                className="object-cover"
              />
            </div>

            <div>
              <h3 className="font-medium text-neutral-900">{Position.name}</h3>
              <span className="text-sm text-gray-600">{Position.category}</span>
            </div>
          </Link>
        ))}
      </HorizontalScroll>
    </section>
  )
}

export default TrendPositionList

import React from 'react'
import { getPositionByRegion } from '@/services/positions'
import Link from 'next/link'
import Image from 'next/image'
import { regionalPositions } from '@/mocks/home/regional-position-list'

const RegionalPositionList = async () => {
  let data = await getPositionByRegion({ provinceId: 1 })

  if (!data) {
    data = regionalPositions
  }

  return (
    <section>
      <h2 className="section-title">{data?.region ?? '서울'}의 주요 직업</h2>

      <div className="space-y-3 px-4">
        {data.positions.map((Position) => (
          <Link
            key={`region_${Position.id}`}
            className="flex items-center gap-4 rounded-lg"
            href={`/Position-description/${Position.id}`}
          >
            <div className="flex-shrink-0">
              <Image
                src={Position.imageUrl}
                alt={Position.name}
                width={48}
                height={48}
                className="rounded-lg object-cover"
              />
            </div>

            <div className="min-w-0 flex-1">
              <h4 className="truncate font-medium text-neutral-900">
                {Position.name}
              </h4>
              <p className="text-sm text-gray-600">{Position.category}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  )
}

export default RegionalPositionList

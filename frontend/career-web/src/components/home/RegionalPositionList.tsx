import React from 'react'
import { getPositionByRegion } from '@/services/positions'
import Link from 'next/link'
import Image from 'next/image'

const RegionalPositionList = async () => {
  const data = await getPositionByRegion({ provinceId: 1 })

  if (!data) {
    return <></>
  }

  return (
    <section>
      <h2 className="section-title">{data?.region ?? '서울'}의 주요 직업</h2>

      <div className="space-y-3 px-4">
        {data.positions.map((position) => (
          <Link
            key={`region_${position.id}`}
            className="flex items-center gap-4 rounded-lg"
            href={`/position-detail/${position.id}`}
          >
            <div className="flex-shrink-0">
              <Image
                src={position.imageUrl}
                alt={position.name}
                width={48}
                height={48}
                className="rounded-lg object-cover"
              />
            </div>

            <div className="min-w-0 flex-1">
              <h4 className="truncate font-medium text-neutral-900">
                {position.name}
              </h4>
              <p className="text-sm text-gray-600">{position.category}</p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  )
}

export default RegionalPositionList

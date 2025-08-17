import React from 'react'
import Image from 'next/image'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'
import { getTrendJobs } from '@/services/positions'

const TrendJobList = async () => {
  const trendJobs = await getTrendJobs()

  return (
    <>
      <section>
        <h2 className="section-title">최신 트렌드 TOP 20 직업</h2>

        <HorizontalScroll>
          {trendJobs.map((job) => (
            <Link
              key={job.id}
              className="flex flex-col gap-3"
              href={`/job-description/${job.id}`}
            >
              <div className="w-40">
                <Image
                  src={job.imageUrl}
                  alt={`job_trend_20_${job.name}`}
                  width={160}
                  height={160}
                  className="rounded-xl object-cover"
                />
              </div>

              <div>
                <h3 className="font-medium text-neutral-900">{job.name}</h3>
                <span className="text-sm text-gray-600">{job.category}</span>
              </div>
            </Link>
          ))}
        </HorizontalScroll>
      </section>
    </>
  )
}

export default TrendJobList

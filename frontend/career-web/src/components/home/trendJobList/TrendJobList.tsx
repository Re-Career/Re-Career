import React from 'react'
import Image from 'next/image'
import { trendJobs } from '@/mocks/home/trend-job-list'
import Link from 'next/link'
import HorizontalScroll from '@/components/common/HorizontalScroll'

const TrendJobList = () => {
  return (
    <>
      <section className="">
        <div>
          <h2 className="section-title">최고의 20개 트렌드 직업</h2>

          <HorizontalScroll>
            {trendJobs.map((job, index) => (
              <Link
                key={job.id}
                className="flex flex-col gap-3"
                href={`/job-description/${job.id}`}
              >
                <div className="w-40">
                  <Image
                    src={job.illustration}
                    alt={job.name}
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
        </div>
      </section>
    </>
  )
}

export default TrendJobList

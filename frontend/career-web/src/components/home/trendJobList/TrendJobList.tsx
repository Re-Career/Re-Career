import React from 'react'
import Image from 'next/image'
import { trendJobs } from '@/mocks/home/trend-job-list'
import Link from 'next/link'

const TrendJobList = () => {
  return (
    <>
      <section className="">
        <div>
          <h2 className="px-4 pt-5 pb-3 text-xl font-bold text-neutral-900">
            최고의 20개 트렌드 직업
          </h2>

          <div className="overflow-x-auto p-4">
            <div className="flex gap-3">
              {trendJobs.map((job) => (
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
                    <span className="text-sm text-gray-600">
                      {job.category}
                    </span>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

export default TrendJobList

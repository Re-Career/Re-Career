import React from 'react'
import { getJobByRegion } from '@/services/positions'
import Link from 'next/link'
import Image from 'next/image'
import { regionalJobs } from '@/mocks/home/regional-job-list'

const RegionalJobList = async () => {
  let data = await getJobByRegion()

  if (!data) {
    data = regionalJobs
  }

  return (
    <section>
      <h2 className="section-title">
        {data?.region ?? '한국'}의 주요 직업 TOP 3
      </h2>

      {data.positions.map((job) => (
        <div key={`region_${job.id}`} className="rounded-lg px-4 py-3">
          <Link
            className="flex items-center gap-4 rounded-lg"
            href={`/job-description/${job.id}`}
          >
            <div className="flex-shrink-0">
              <Image
                src={job.imageUrl}
                alt={job.name}
                width={48}
                height={48}
                className="rounded-lg object-cover"
              />
            </div>

            <div className="min-w-0 flex-1">
              <h4 className="truncate font-medium text-neutral-900">
                {job.name}
              </h4>
              <p className="text-sm text-gray-600">{job.category}</p>
            </div>
          </Link>
        </div>
      ))}
    </section>
  )
}

export default RegionalJobList

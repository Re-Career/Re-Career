import { regionalJobs } from '@/mocks/home/regional-job-list'
import Image from 'next/image'
import React from 'react'

const RegionalJobList = () => {
  const { region, jobs } = regionalJobs

  return (
    <section>
      <div>
        <h2 className="px-4 pt-5 pb-3 text-xl font-bold text-neutral-900">
          {region}의 주요 직업 TOP 3
        </h2>

        {jobs.map((job) => (
          <div key={`region_${job.id}`} className="rounded-lg px-4 py-3">
            <div className="flex items-center gap-4 rounded-lg">
              <div className="flex-shrink-0">
                <Image
                  src={job.illustration}
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
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}

export default RegionalJobList

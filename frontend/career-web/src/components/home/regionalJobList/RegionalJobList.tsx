import { regionalJobs } from '@/mocks/home/regional-job-list'
import React from 'react'
import RegionalJob from './RegionalJob'

const RegionalJobList = () => {
  const { region, jobs } = regionalJobs

  return (
    <section>
      <div>
        <h2 className="px-4 pt-5 pb-3 text-xl font-bold text-neutral-900">
          {region}의 주요 직업 TOP 3
        </h2>

        {jobs.map((job) => (
          <RegionalJob key={`job_list_${job.id}`} job={job} />
        ))}
      </div>
    </section>
  )
}

export default RegionalJobList

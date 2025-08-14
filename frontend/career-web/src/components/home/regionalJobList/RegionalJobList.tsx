import { regionalJobs } from '@/mocks/home/regional-job-list'
import React from 'react'
import RegionalJob from './RegionalJob'

const RegionalJobList = () => {
  const { region, jobs } = regionalJobs

  return (
    <section>
      <div>
        <h2 className="section-title">{region}의 주요 직업 TOP 3</h2>

        {jobs.map((job) => (
          <RegionalJob key={`job_list_${job.id}`} job={job} />
        ))}
      </div>
    </section>
  )
}

export default RegionalJobList

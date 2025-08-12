'use client'

import { RegionJob } from '@/types/home'
import Image from 'next/image'
import React from 'react'

const RegionalJob = ({ job }: { job: RegionJob }) => {
  const handleJobList = () => {
    console.log('here')
  }
  return (
    <div key={`region_${job.id}`} className="rounded-lg px-4 py-3">
      <div
        className="flex items-center gap-4 rounded-lg"
        onClick={handleJobList}
      >
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
          <h4 className="truncate font-medium text-neutral-900">{job.name}</h4>
          <p className="text-sm text-gray-600">{job.category}</p>
        </div>
      </div>
    </div>
  )
}

export default RegionalJob

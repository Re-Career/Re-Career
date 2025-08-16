import { JobDetail, DefaultJob, RegionJob } from '@/types/position'
import { fetchUrl } from './api'

export const getTrendJobs = async (): Promise<DefaultJob[]> => {
  const { data } = await fetchUrl('/positions/trend-20', {
    next: { revalidate: 24 * 60 * 60 },
  })

  return data
}

export const getJobDetail = async (id: string): Promise<JobDetail> => {
  const { data } = await fetchUrl(`/positions/${id}`)

  return data
}

export const getJobByRegion = async (region?: string): Promise<RegionJob> => {
  const { data } = await fetchUrl(`/positions/by-region/${region ?? 'all'}`, {
    next: { revalidate: 24 * 60 * 60 },
  })

  return data
}

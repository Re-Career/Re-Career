import { JobDetail, DefaultJob, RegionJob } from '@/types/position'
import { fetchUrl } from './api'

export const getTrendJobs = async (): Promise<DefaultJob[]> => {
  const res = await fetchUrl('/positions/trend-20', {
    next: { revalidate: 24 * 60 * 60 },
  })
  const { data } = await res.json()

  return data
}

export const getJobDetail = async (id: string): Promise<JobDetail> => {
  const res = await fetchUrl(`/positions/${id}`)
  const { data } = await res.json()

  return data
}

export const getJobByRegion = async (region?: string): Promise<RegionJob> => {
  const res = await fetchUrl(`/positions/by-region/${region ?? 'all'}`, {
    next: { revalidate: 24 * 60 * 60 },
  })
  const { data } = await res.json()

  return data
}

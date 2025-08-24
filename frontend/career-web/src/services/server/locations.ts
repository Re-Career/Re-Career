import { City, Province } from '@/types/location'
import { fetchUrl } from '../api'

export const getProvinces = async (): Promise<Province[]> => {
  const res = await fetchUrl('/locations/provinces', { cache: 'force-cache' })
  const { data } = await res.json()

  return data
}

export const getCities = async (): Promise<City[]> => {
  const res = await fetchUrl('/locations/cities', { cache: 'force-cache' })
  const { data } = await res.json()

  return data
}

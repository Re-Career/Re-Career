import { City, Province } from '@/types/location'
import { fetchUrl } from '../api'
import { FetchResponse } from '@/types/global'

export const getProvinces = async (): Promise<FetchResponse<Province[]>> => {
  return await fetchUrl<Province[]>('/locations/provinces', { 
    cache: 'force-cache',
    next: { tags: ['locations', 'provinces'] }
  })
}

export const getCities = async (): Promise<FetchResponse<City[]>> => {
  return await fetchUrl<City[]>('/locations/cities', { 
    cache: 'force-cache',
    next: { tags: ['locations', 'cities'] }
  })
}

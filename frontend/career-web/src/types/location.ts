export interface Province {
  id: number
  key: string
  name: string
}

export interface City extends Province {
  provinceId: number
}

export interface UserLocation {
  provinceId: number
  cityId?: number
}

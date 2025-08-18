export interface Province {
  id: number
  key: string
  name: string
}

export interface City extends Province {
  provinceId: number
}

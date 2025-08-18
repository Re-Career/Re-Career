'use client'

import { useState, useMemo } from 'react'
import Dropdown from '@/components/common/Dropdown'
import { City, Province } from '@/types/location'

interface RegionSelectorProps {
  onProvinceChange: (provinceId: number) => void
  onCityChange: (cityId: number) => void
  provinces: Province[]
  cities: City[]
}

const RegionSelector = ({
  onProvinceChange,
  onCityChange,
  provinces,
  cities,
}: RegionSelectorProps) => {
  const [selectedProvinceKey, setSelectedProvinceKey] = useState<string>('')
  const [selectedCityKey, setSelectedCityKey] = useState<string>('')

  const provinceOptions = useMemo(
    () =>
      provinces.map((province) => ({
        value: province.key,
        label: province.name,
      })),
    []
  )

  const cityOptions = useMemo(() => {
    const currentProvince = provinces.find(
      (province) => province.key === selectedProvinceKey
    )

    return cities
      .filter((city) => city.provinceId === currentProvince?.id)
      .map((city) => ({
        value: city.key,
        label: city.name,
      }))
  }, [selectedProvinceKey])

  const handleProvinceChange = (value: string) => {
    setSelectedProvinceKey(value)
    setSelectedCityKey('')

    onProvinceChange(provinces.find((p) => p.key === value)?.id || 0)
  }

  const handleCityChange = (value: string) => {
    setSelectedCityKey(value)

    onCityChange(cities.find((city) => city.key === value)?.id || 0)
  }

  return (
    <div className="flex gap-1">
      <Dropdown
        value={selectedProvinceKey}
        onChange={handleProvinceChange}
        options={provinceOptions}
        placeholder="도/시 *"
        required
        className="flex-1"
      />
      <Dropdown
        value={selectedCityKey}
        onChange={handleCityChange}
        options={cityOptions}
        placeholder="시/군/구 (선택)"
        disabled={!selectedProvinceKey}
        className="flex-1"
      />
    </div>
  )
}

export default RegionSelector

import React, { useState, useMemo } from 'react'
import { CITIES_LIST, PROVINCES } from '@/lib/constants/regions'
import Dropdown from '@/components/ui/Dropdown'

interface RegionSelectorProps {
  onRegionChange: (regionName: string) => void
}

export const getCitiesByProvinceId = (provinceId: number) => {
  return CITIES_LIST.filter((city) => city.provinceId === provinceId)
}

export const getCitiesByProvinceKey = (provinceKey: string) => {
  const province = PROVINCES.find((p) => p.key === provinceKey)

  return province ? getCitiesByProvinceId(province.id) : []
}

const RegionSelector = ({ onRegionChange }: RegionSelectorProps) => {
  const [selectedProvinceKey, setSelectedProvinceKey] = useState<string>('')
  const [selectedCityKey, setSelectedCityKey] = useState<string>('')

  const selectedProvince = useMemo(
    () => PROVINCES.find((p) => p.key === selectedProvinceKey),
    [selectedProvinceKey]
  )

  const availableCities = useMemo(
    () =>
      selectedProvinceKey ? getCitiesByProvinceKey(selectedProvinceKey) : [],
    [selectedProvinceKey]
  )

  const provinceOptions = useMemo(
    () =>
      PROVINCES.map((province) => ({
        value: province.key,
        label: province.name,
      })),
    []
  )

  const cityOptions = useMemo(
    () =>
      availableCities.map((city) => ({
        value: city.key,
        label: city.name,
      })),
    [availableCities]
  )

  const handleProvinceChange = (value: string) => {
    setSelectedProvinceKey(value)
    setSelectedCityKey('')

    const province = PROVINCES.find((p) => p.key === value)

    if (province) {
      onRegionChange(province.name)
    }
  }

  const handleCityChange = (value: string) => {
    setSelectedCityKey(value)

    const selectedRegionName =
      availableCities.find((c) => c.key === value)?.name ||
      selectedProvince?.name ||
      ''

    onRegionChange(selectedRegionName)
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

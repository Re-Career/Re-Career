'use client'

import { FilterOptions } from '@/types/mentor'
import { filterOptions } from '@/mocks/matching/filterOptions'
import React from 'react'
import Link from 'next/link'

interface FilterDropdownProps {
  isOpen: boolean
  mentorName: string
  filters: FilterOptions
  onFilterChange: (key: string, value: string) => void
  handleReset: () => void
  onClose: () => void
}

const FilterDropdown = ({
  isOpen,
  mentorName,
  filters,
  onFilterChange,
  handleReset,
  onClose,
}: FilterDropdownProps) => {
  const handleSearch = () => {
    const params = new URLSearchParams()

    if (mentorName) params.set('search', mentorName)

    // 동적으로 모든 필터 키 처리
    Object.entries(filters).forEach(([key, values]) => {
      if (Array.isArray(values) && values.length > 0) {
        params.set(key, values.join(','))
      }
    })

    // URL 생성
    const searchString = params.toString()
    return searchString ? `?${searchString}` : ''
  }

  return (
    <div
      className={`flex transform flex-col gap-4 overflow-hidden rounded-lg bg-white shadow-xl transition-all duration-300 ease-out ${
        isOpen
          ? 'max-h-[calc(100vh-104px)] py-4 opacity-100'
          : 'max-h-0 py-0 opacity-0'
      }`}
    >
      <div className="flex-1 space-y-4 overflow-y-auto px-4">
        {filterOptions.map(({ key, title, options }) => (
          <div key={key}>
            <h3 className="mb-3 font-medium">{title}</h3>
            <div className="flex flex-wrap gap-2">
              {options.map((option, index) => {
                const filterArray = filters[key]
                const isSelected =
                  Array.isArray(filterArray) && filterArray.includes(option.key)

                return (
                  <button
                    key={option.key + index}
                    onClick={() => onFilterChange(key, option.key)}
                    className={`rounded-full border px-4 py-2 text-sm ${
                      isSelected
                        ? 'bg-secondary border-secondary'
                        : 'border-gray-300 bg-white text-gray-700'
                    }`}
                  >
                    {option.name}
                  </button>
                )
              })}
            </div>
          </div>
        ))}
      </div>
      <div className="flex gap-2 px-4">
        <button
          className="flex-1 rounded border border-gray-300 p-2 font-semibold"
          onClick={handleReset}
        >
          초기화
        </button>
        <Link
          href={`/matching${handleSearch()}`}
          className="bg-primary flex-3 rounded p-2 text-center font-semibold"
          onClick={onClose}
        >
          적용하기
        </Link>
      </div>
    </div>
  )
}

export default FilterDropdown

'use client'

import React from 'react'
import Link from 'next/link'
import { FilterConfig } from '@/types/mentor'
import { DefaultData } from '@/types/global'
import { IoClose as CloseIcon } from 'react-icons/io5'

interface FilterDropdownProps {
  isOpen: boolean
  mentorName: string
  filters: FilterConfig[]
  selectedFilters: FilterConfig[]
  onFilterChange: ({
    filterConfig,
    option,
  }: {
    filterConfig: FilterConfig
    option: DefaultData
  }) => void
  handleReset: () => void
  onClose: () => void
}

const FilterDropdown = ({
  isOpen,
  mentorName,
  filters,
  selectedFilters,
  onFilterChange,
  handleReset,
  onClose,
}: FilterDropdownProps) => {
  const generateSearchUrl = () => {
    const params = new URLSearchParams()

    if (mentorName.trim()) {
      params.set('keyword', mentorName)
    }

    selectedFilters.forEach((filter) => {
      if (filter.options.length > 0) {
        const paramKey = `${filter.key}Ids`.replace(/sIds$/, 'Ids')
        const values = filter.options.map((option) => option.id.toString())

        params.set(paramKey, values.join(','))
      }
    })

    return params.toString() ? `?${params.toString()}` : ''
  }

  return (
    <div
      className={`flex transform flex-col gap-4 overflow-hidden rounded-lg bg-white shadow-xl transition-all duration-300 ease-out ${
        isOpen
          ? 'max-h-[calc(100vh-96px)] pt-2 pb-4 opacity-100'
          : 'max-h-0 py-0 opacity-0'
      }`}
    >
      <div className="flex-1 space-y-4 overflow-y-auto px-4">
        {filters.map((filterConfig) => {
          const { key, title, options } = filterConfig

          return (
            <div key={key}>
              <h3 className="mb-3 font-medium">{title}</h3>
              <div className="flex flex-wrap gap-2">
                {options.map((option) => {
                  const isSelected = selectedFilters.some(
                    (filter) =>
                      filter.key === key &&
                      filter.options.some(
                        (selectedOption) => selectedOption.id === option.id
                      )
                  )

                  return (
                    <button
                      key={`${key}_${option.id}`}
                      onClick={() => onFilterChange({ filterConfig, option })}
                      className={`rounded-full border px-4 py-2 text-sm transition-all duration-200 ease-in-out ${
                        isSelected
                          ? 'border-secondary-500 bg-secondary-500'
                          : 'border-gray-300 bg-white text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      {option.name}
                    </button>
                  )
                })}
              </div>
            </div>
          )
        })}
      </div>
      <div className="flex gap-2 px-4">
        <button
          type="button"
          className="flex-1 rounded border border-gray-300 p-2 font-semibold text-gray-700 transition-colors hover:bg-gray-50"
          onClick={handleReset}
        >
          초기화
        </button>
        <Link
          href={`/matching${generateSearchUrl()}`}
          className="bg-primary-500 hover:bg-primary-600 flex-[3] rounded p-2 text-center font-semibold transition-colors"
          onClick={onClose}
        >
          적용하기
        </Link>
      </div>
      {isOpen && (
        <button className="fixed top-4 left-4 h-6 w-6" onClick={onClose}>
          <CloseIcon className="h-6 w-6" />
        </button>
      )}
    </div>
  )
}

export default FilterDropdown

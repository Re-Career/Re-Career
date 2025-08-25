'use client'

import { useState, useEffect, useRef } from 'react'
import { IoSearch, IoFilter } from 'react-icons/io5'
import FilterDropdown from './FilterDropdown'
import { FilterConfig } from '@/types/mentor'
import { getFilterOptions } from '@/services/server/mentor'
import { DefaultData } from '@/types/global'
import useSWR from 'swr'

interface FilterProps {
  initialFilterConfigs: Record<string, string[]>
  initialMentorName: string
}

const Filter = ({ initialFilterConfigs, initialMentorName }: FilterProps) => {
  const [mentorName, setMentorName] = useState(initialMentorName)
  const [selectedFilters, setSelectedFilters] = useState<FilterConfig[]>([])
  const [isFilterOpen, setIsFilterOpen] = useState(false)

  const { data: filterOptionsResponse, isLoading: isFilterOptionsLoading } =
    useSWR(
      'filter-options',
      async () => {
        const { data } = await getFilterOptions()

        return data
      },
      {
        revalidateOnFocus: false,
        revalidateOnReconnect: false,
        dedupingInterval: Infinity, // 영구 저장
      }
    )

  const filterOptions = filterOptionsResponse || []

  const filterRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    // URL 파라미터 키를 실제 필터 키로 변환
    const transformUrlKeyToFilterKey = (urlKey: string): string => {
      if (urlKey.endsWith('Ids')) return `${urlKey.slice(0, -3)}s`

      return urlKey
    }

    // filterOptions가 로딩 중이면 초기화하지 않음
    if (isFilterOptionsLoading || filterOptions.length === 0) return

    const initialFilters = Object.entries(initialFilterConfigs)
      .map(([urlKey, optionIds]) => {
        const filterKey = transformUrlKeyToFilterKey(urlKey)
        const filterConfig = filterOptions.find(
          (config) => config.key === filterKey
        )

        if (filterConfig) {
          const selectedOptions = filterConfig.options.filter((option) =>
            optionIds.includes(option.id.toString())
          )

          return {
            key: filterKey,
            title: filterConfig.title,
            options: selectedOptions,
          }
        }

        return null
      })
      .filter((filter): filter is FilterConfig => filter !== null)

    setSelectedFilters(initialFilters)
    setMentorName(initialMentorName)
  }, [initialFilterConfigs, initialMentorName, filterOptions])

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        filterRef.current &&
        !filterRef.current.contains(event.target as Node)
      ) {
        setIsFilterOpen(false)
      }
    }

    if (isFilterOpen) {
      document.documentElement.style.overflow = 'hidden'
      document.addEventListener('mousedown', handleClickOutside)
    } else {
      document.documentElement.style.overflow = ''
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
      document.documentElement.style.overflow = ''
    }
  }, [isFilterOpen])

  const handleMentorNameChange = (value: string) => {
    setMentorName(value)
  }

  const handleFilterChange = ({
    filterConfig,
    option,
  }: {
    filterConfig: FilterConfig
    option: DefaultData
  }) => {
    setSelectedFilters((prevFilters) => {
      const existingFilterIndex = prevFilters.findIndex(
        (filter) => filter.key === filterConfig.key
      )

      if (existingFilterIndex !== -1) {
        const existingFilter = prevFilters[existingFilterIndex]
        const isOptionSelected = existingFilter.options.some(
          (existingOption) => existingOption.id === option.id
        )

        return prevFilters
          .map((filter, index) =>
            index === existingFilterIndex
              ? {
                  ...filter,
                  options: isOptionSelected
                    ? filter.options.filter(
                        (existingOption) => existingOption.id !== option.id
                      )
                    : [...filter.options, option],
                }
              : filter
          )
          .filter((filter) => filter.options.length > 0)
      }

      return [...prevFilters, { ...filterConfig, options: [option] }]
    })
  }

  const handleReset = () => {
    setSelectedFilters([])
  }

  return (
    <>
      <div
        className={`fixed inset-0 z-60 bg-black transition-opacity duration-300 ease-in-out ${
          isFilterOpen
            ? 'pointer-events-auto opacity-10'
            : 'pointer-events-none opacity-0'
        }`}
        onClick={() => setIsFilterOpen(false)}
      />

      <div
        ref={filterRef}
        className="fixed top-14 right-0 left-0 z-80 mx-auto max-w-[450px] bg-white"
      >
        <div className="flex px-4">
          <div className="focus-within:ring-primary-500 mb-2 flex flex-1 items-center gap-2 rounded-lg bg-gray-100 px-4 focus-within:ring-2">
            <IoSearch className="h-6 w-6 text-gray-400" />
            <input
              type="text"
              placeholder="멘토 검색"
              value={mentorName}
              onChange={(e) => handleMentorNameChange(e.target.value)}
              onFocus={() => setIsFilterOpen(true)}
              className="w-full flex-1 bg-transparent outline-none"
            />
          </div>
          <button
            onClick={() => setIsFilterOpen((prev) => !prev)}
            className="flex h-10 w-10 items-center justify-center"
          >
            <IoFilter />
          </button>
        </div>

        <FilterDropdown
          isOpen={isFilterOpen}
          filters={filterOptions}
          selectedFilters={selectedFilters}
          mentorName={mentorName}
          onFilterChange={handleFilterChange}
          handleReset={handleReset}
          onClose={() => setIsFilterOpen(false)}
        />
      </div>
    </>
  )
}

export default Filter

'use client'

import { useState, useEffect, useRef } from 'react'
import { IoSearch, IoFilter } from 'react-icons/io5'
import FilterDropdown from './FilterDropdown'
import { useRouter } from 'next/navigation'
import { FilterOptions } from '@/types/mentor'
import { INITIAL_FILTERS } from '@/lib/constants/matching'

interface FilterProps {
  initialFilters: FilterOptions
  initialMentorName: string
}

const Filter = ({ initialFilters, initialMentorName }: FilterProps) => {
  const [mentorName, setMentorName] = useState(initialMentorName)
  const [filters, setFilters] = useState(initialFilters)
  const [isFilterOpen, setIsFilterOpen] = useState(false)

  const filterRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    setFilters(initialFilters)
    setMentorName(initialMentorName)
  }, [initialFilters, initialMentorName])

  // 드롭다운 외부 클릭 시 닫기 & 스크롤 제어
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

  const handleMentorName = (value: string) => {
    setMentorName(value)
  }

  const handleFilterChange = (key: keyof typeof filters, value: string) => {
    let newFilters = { ...filters }

    // 모든 필터는 배열로 관리 (다중 선택)
    const currentArray = Array.isArray(filters[key]) ? filters[key] : []
    if (currentArray.includes(value)) {
      newFilters[key] = currentArray.filter((item) => item !== value)
    } else {
      newFilters[key] = [...currentArray, value]
    }

    setFilters(newFilters)
  }

  const handleReset = () => {
    setFilters(INITIAL_FILTERS)
  }

  return (
    <>
      <div
        className={`pointer-events-none fixed inset-0 z-60 bg-black transition-opacity duration-300 ease-in-out ${
          isFilterOpen ? 'pointer-events-auto opacity-10' : 'opacity-0'
        }`}
        onClick={() => setIsFilterOpen(false)}
      />

      <div
        ref={filterRef}
        className="fixed top-14 right-0 left-0 z-80 mx-auto max-w-[450px] bg-white pt-2"
      >
        <div className="flex px-4">
          <div className="focus-within:ring-primary flex flex-1 items-center gap-2 rounded-lg bg-gray-100 px-4 py-2 focus-within:ring-2">
            <IoSearch className="h-6 w-6 text-gray-400" />
            <input
              type="text"
              placeholder="멘토 검색"
              value={mentorName}
              onChange={(e) => handleMentorName(e.target.value)}
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
          filters={filters}
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

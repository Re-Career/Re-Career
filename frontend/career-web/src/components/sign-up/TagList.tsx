'use client'

import { PersonalityTag } from '@/types/personality-tags'
import React from 'react'

interface TagListProps {
  selectedTags: number[]
  toggleTag: (tag: number) => void
  tags: PersonalityTag[]
}

const TagList = ({ selectedTags, toggleTag, tags }: TagListProps) => {
  if (!tags || tags.length === 0) {
    return <></>
  }

  return (
    <div className="flex flex-wrap gap-2">
      {tags.map((tag: PersonalityTag) => (
        <label
          key={tag.id}
          className="cursor-pointer"
          onClick={() => toggleTag(tag.id)}
        >
          <span
            className={`inline-block rounded-full px-4 py-2 text-sm font-medium transition-colors ${
              selectedTags.includes(tag.id)
                ? 'bg-secondary'
                : 'bg-gray-100 text-neutral-900 hover:bg-gray-200'
            }`}
          >
            {tag.name}
          </span>
        </label>
      ))}
    </div>
  )
}

export default TagList

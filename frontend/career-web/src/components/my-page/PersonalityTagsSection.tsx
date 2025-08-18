'use client'

import { useState } from 'react'
import { PersonalityTagsEditor } from './profile'
import BottomSheet from '@/components/common/BottomSheet'
import { PersonalityTag } from '@/types/personality-tags'
import { MdOutlineEdit as EditIcon } from 'react-icons/md'

interface PersonalityTagsSectionProps {
  personalityTags: PersonalityTag[]
  allTags: PersonalityTag[]
}

const PersonalityTagsSection = ({
  personalityTags,
  allTags,
}: PersonalityTagsSectionProps) => {
  const [showPersonalityEditor, setShowPersonalityEditor] = useState(false)

  return (
    <>
      <section>
        <div className="mb-6">
          <div className="flex items-center gap-1">
            <h2 className="p-4 pr-0 text-xl font-bold text-neutral-900">
              성향 태그
            </h2>
            <button
              onClick={() => setShowPersonalityEditor(true)}
              className="rounded-lg p-1.5 transition-colors hover:bg-gray-100"
            >
              <EditIcon className="h-4 w-4 text-gray-600" />
            </button>
          </div>
          {personalityTags && personalityTags.length > 0 ? (
            <div className="flex flex-wrap gap-2 px-4">
              {personalityTags.map((tag: any, index: number) => (
                <span
                  key={index}
                  className="bg-primary-500 rounded-full px-3 py-1 text-sm font-medium"
                >
                  {typeof tag === 'string' ? tag : tag.name}
                </span>
              ))}
            </div>
          ) : (
            <div className="px-4 text-sm text-gray-500">
              성향 태그를 설정해보세요
            </div>
          )}
        </div>
      </section>

      <BottomSheet
        isOpen={showPersonalityEditor}
        onClose={() => setShowPersonalityEditor(false)}
      >
        <PersonalityTagsEditor
          allTags={allTags}
          currentTags={personalityTags || []}
          onClose={() => setShowPersonalityEditor(false)}
        />
      </BottomSheet>
    </>
  )
}

export default PersonalityTagsSection

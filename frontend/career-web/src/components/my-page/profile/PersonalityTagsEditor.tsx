'use client'

import { useState } from 'react'
import { PersonalityTag } from '@/types/personality-tags'
import { updatePersonalityTagsAction } from '@/app/actions/user/action'

interface PersonalityTagsEditorProps {
  currentTags: PersonalityTag[]
  onClose: () => void
  allTags: PersonalityTag[]
}

const PersonalityTagsEditor = ({
  currentTags,
  onClose,
  allTags,
}: PersonalityTagsEditorProps) => {
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>(
    currentTags.map((tag) => tag.id)
  )

  const [isSaving, setIsSaving] = useState(false)

  const handleTagToggle = (tagId: number) => {
    setSelectedTagIds((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId]
    )
  }

  const handleSave = async () => {
    setIsSaving(true)

    try {
      const result = await updatePersonalityTagsAction(selectedTagIds)

      if (result.success) {
        onClose()
      }
    } catch (error) {
      console.error('Failed to update personality tags:', error)
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className="flex h-full min-h-0 w-full flex-col">
      <div className="flex flex-shrink-0 justify-center pt-3 pb-6">
        <div className="h-1 w-12 rounded-full bg-gray-300" />
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto px-6">
        <div className="mb-6">
          <h3 className="mb-2 text-xl font-bold text-gray-900">
            성향 태그 수정
          </h3>
          <p className="text-sm text-gray-600">
            본인의 성향과 맞는 태그를 선택해주세요 (최대 5개)
          </p>
        </div>

        <div className="pb-6">
          <div className="flex flex-wrap gap-2">
            {allTags.map((tag) => (
              <button
                disabled={
                  selectedTagIds.length === 5 &&
                  !selectedTagIds.includes(tag.id)
                }
                key={tag.id}
                className={`transition-colors ${
                  selectedTagIds.length === 5 &&
                  !selectedTagIds.includes(tag.id)
                    ? 'cursor-not-allowed opacity-50'
                    : 'cursor-pointer'
                }`}
                onClick={() => handleTagToggle(tag.id)}
              >
                <span
                  className={`inline-block rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                    selectedTagIds.includes(tag.id)
                      ? 'bg-secondary-500'
                      : selectedTagIds.length === 5 &&
                          !selectedTagIds.includes(tag.id)
                        ? 'bg-gray-100 text-neutral-400'
                        : 'bg-gray-100 text-neutral-900 hover:bg-gray-200'
                  }`}
                >
                  {tag.name}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="flex-shrink-0 border-t border-gray-100 bg-white p-6">
        <div className="flex gap-1">
          <button
            onClick={onClose}
            disabled={isSaving}
            className="w-full flex-1 rounded-lg border border-gray-300 px-4 py-4 font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-50"
          >
            취소
          </button>
          <button
            onClick={handleSave}
            disabled={isSaving}
            className="bg-primary-500 hover:bg-primary-600 w-full flex-1 rounded-lg px-4 py-4 font-semibold disabled:opacity-50"
          >
            {isSaving ? '저장 중...' : '저장하기'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default PersonalityTagsEditor

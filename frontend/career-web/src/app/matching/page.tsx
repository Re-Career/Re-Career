'use client'

import React, { useState } from 'react'
import Image from 'next/image'
import Header from '@/components/common/Header'
import { searchMentors } from '@/mocks/home/mentors-search'
import MentorProfileButton from '@/components/mentor/MentorProfileButton'

const MatchingPage = () => {
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedJob, setSelectedJob] = useState('')
  const [selectedLocation, setSelectedLocation] = useState('')
  const [selectedMeetingType, setSelectedMeetingType] = useState('')

  // 추천 매칭 멘토 (처음 3명)
  const recommendedMentors = searchMentors.slice(0, 3)

  // 전체 멘토 리스트 (필터링)
  const filteredMentors = searchMentors.filter((mentor) => {
    return (
      mentor.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (selectedJob === '' || mentor.job.includes(selectedJob)) &&
      (selectedLocation === '' || mentor.location === selectedLocation) &&
      (selectedMeetingType === '' ||
        mentor.meetingType === selectedMeetingType ||
        mentor.meetingType === 'both')
    )
  })

  return (
    <>
      <Header title="멘토 찾기" />
      {/* 검색 섹션 */}
      <div className="flex flex-col gap-4 pb-4">
        <div className="sticky top-14 space-y-4 bg-white p-4">
          <div>
            <input
              type="text"
              placeholder="멘토 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="focus:ring-primary w-full rounded-lg bg-gray-100 p-4 px-4 py-2 focus:ring-2 focus:outline-none"
            />
          </div>
          {/* 필터 */}
          <div className="overflow-x-auto">
            <div className="flex gap-3">
              <select
                value={selectedJob}
                onChange={(e) => setSelectedJob(e.target.value)}
                className="w-20 rounded-md bg-gray-100 px-3 py-2 text-sm"
              >
                <option value="">직업</option>
                <option value="소프트웨어">소프트웨어</option>
                <option value="프로덕트">프로덕트</option>
                <option value="디자이너">디자이너</option>
                <option value="데이터">데이터</option>
                <option value="마케팅">마케팅</option>
              </select>

              <select className="rounded-md bg-gray-100 px-3 py-2 text-sm">
                <option value="">경험</option>
                <option value="1-3">1-3년</option>
                <option value="4-6">4-6년</option>
                <option value="7+">7년 이상</option>
              </select>

              <select
                value={selectedLocation}
                onChange={(e) => setSelectedLocation(e.target.value)}
                className="rounded-md bg-gray-100 px-3 py-2 text-sm"
              >
                <option value="">지역</option>
                <option value="서울">서울</option>
                <option value="부산">부산</option>
                <option value="대구">대구</option>
              </select>

              <select
                value={selectedMeetingType}
                onChange={(e) => setSelectedMeetingType(e.target.value)}
                className="rounded-md bg-gray-100 px-3 py-2 text-sm"
              >
                <option value="">미팅 방식</option>
                <option value="online">온라인</option>
                <option value="offline">오프라인</option>
                <option value="both">온/오프</option>
              </select>

              <select className="rounded-md bg-gray-100 px-3 py-2 text-sm">
                <option value="">성향</option>
                <option value="친근">친근함</option>
                <option value="논리적">논리적</option>
                <option value="창의적">창의적</option>
                <option value="차분">차분함</option>
              </select>
            </div>
          </div>
        </div>
        {/* 추천 매칭 섹션 */}
        <div className="border-b border-gray-100 px-4 pb-4">
          <h2 className="mb-4 text-lg font-bold text-gray-900">추천 매칭</h2>
          <div className="overflow-x-auto">
            <div className="flex gap-4">
              {recommendedMentors.map((mentor) => (
                <div
                  key={mentor.id}
                  className="flex-shrink-0 cursor-pointer rounded-lg bg-white"
                  onClick={() => {
                    // React Native WebView로 메시지 전송
                    if (
                      typeof window !== 'undefined' &&
                      (window as any).ReactNativeWebView
                    ) {
                      ;(window as any).ReactNativeWebView.postMessage(
                        JSON.stringify({
                          type: 'MENTOR_PROFILE',
                          data: { mentorId: mentor.id },
                        })
                      )
                    } else {
                      // 일반 웹 브라우저에서는 일반적인 링크로 이동
                      window.location.href = `mentor/${mentor.id}/profile`
                    }
                  }}
                >
                  <div className="mb-3 h-40 w-40">
                    <Image
                      src={mentor.profileImage}
                      alt={mentor.name}
                      width={160}
                      height={160}
                      className="h-full w-full rounded-lg object-cover"
                    />
                  </div>
                  <div>
                    <h3 className="mb-1 font-semibold text-gray-900">
                      {mentor.name}
                    </h3>
                    <p className="mb-1 text-sm text-gray-600">{mentor.job}</p>
                    <p className="text-xs text-gray-500">
                      {mentor.company} • {mentor.experience}년
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* 전체 멘토 리스트 */}
        <div className="px-4">
          <div className="space-y-5">
            {filteredMentors.map((mentor) => (
              <div key={`mentor_${mentor.id}`}>
                <div className="flex items-center gap-4">
                  <div className="h-14 w-14 flex-shrink-0 overflow-hidden rounded-full">
                    <Image
                      src={mentor.profileImage}
                      alt={mentor.name}
                      width={56}
                      height={56}
                      className="h-full w-full object-cover"
                    />
                  </div>

                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold text-gray-900">
                          {mentor.name}
                          <span className="ml-1 text-sm text-gray-500">
                            {mentor.job}
                          </span>
                        </h3>

                        <p className="text-xs text-gray-900">
                          {mentor.company} • {mentor.experience}년 •
                          {mentor.location}
                        </p>
                        <p className="text-xs text-gray-900">
                          {mentor.personality}
                        </p>
                      </div>

                      <MentorProfileButton id={mentor.id} />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  )
}

export default MatchingPage

import React from 'react'
import Image from 'next/image'
import { jobDetails } from '@/mocks/home/job-details'
import { notFound } from 'next/navigation'
import Header from '@/components/common/Header'
import SearchMentorButton from '@/components/job-description/SearchMentorButton'

const JobDescriptionPage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params
  const jobDetail = jobDetails.find((job) => job.id === parseInt(id))

  if (!jobDetail) {
    notFound()
  }

  return (
    <>
      <Header title="직무설명" showBackButton />
      <div className="h-60 w-full flex-shrink-0 overflow-hidden">
        <Image
          src={jobDetail.famousPerson.image}
          alt={jobDetail.famousPerson.name}
          width={400}
          height={300}
          className="h-full w-full object-cover object-top"
        />
      </div>
      <section className="mx-auto max-w-4xl px-4 py-6">
        <div className="flex items-center gap-6">
          <div className="flex-1">
            <div className="mb-2 flex items-center gap-3">
              <h1 className="text-2xl font-bold text-neutral-900">
                {jobDetail.name}
              </h1>
              <span className="rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-800">
                {jobDetail.category}
              </span>
            </div>

            <div className="mb-2 text-sm text-gray-600">
              대표 인물:{' '}
              <span className="font-medium text-gray-800">
                {jobDetail.famousPerson.name}
              </span>
              <span className="mx-1">·</span>
              <span>{jobDetail.famousPerson.title}</span>
            </div>

            <p className="leading-relaxed text-gray-700">
              {jobDetail.description}
            </p>
          </div>
        </div>
      </section>
      <section className="p-4">
        <h2 className="mb-4 text-xl font-bold text-neutral-900">주요 책임</h2>

        <div className="space-y-4">
          {jobDetail.responsibilities.map((responsibility, index) => (
            <div key={index} className="flex items-center gap-4">
              <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-gray-200 text-2xl">
                {responsibility.icon}
              </div>
              <div>
                <h3 className="mb-1 font-semibold text-neutral-900">
                  {responsibility.title}
                </h3>
                <p className="text-sm text-gray-600">
                  {responsibility.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>
      <section className="p-4">
        <h2 className="mb-4 text-xl font-bold text-neutral-900">산업 동향</h2>
        <div className="space-y-4">
          {jobDetail.industryTrends.map((trend, index) => (
            <div key={index} className="flex items-start gap-3">
              <div className="mt-0.5 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-gray-400">
                <span className="text-sm font-semibold text-white">
                  {index + 1}
                </span>
              </div>
              <p className="leading-relaxed text-gray-700">{trend}</p>
            </div>
          ))}
        </div>
      </section>
      <div className="flex items-center justify-center p-4">
        <SearchMentorButton jobId={id} />
      </div>
    </>
  )
}

export default JobDescriptionPage

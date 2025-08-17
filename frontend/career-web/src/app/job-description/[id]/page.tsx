import React from 'react'
import Image from 'next/image'
import { notFound } from 'next/navigation'
import Header from '@/components/common/Header'
import Link from 'next/link'
import { getJobDetail } from '@/services/positions'
import PageWithHeader from '@/components/ui/PageWithHeader'

const JobDescriptionPage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params
  const jobDetail = await getJobDetail(id)

  if (!jobDetail) {
    notFound()
  }

  return (
    <>
      <Header title="직무설명" showBackButton />
      <PageWithHeader>
        <div className="h-60 w-full flex-shrink-0 overflow-hidden">
          <Image
            src={jobDetail.imageUrl}
            alt={`job_detail_image_${jobDetail.id}`}
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

              <p className="leading-relaxed text-gray-700">
                {jobDetail.description}
              </p>
            </div>
          </div>
        </section>
        <section className="p-4">
          <h2 className="mb-4 text-xl font-bold text-neutral-900">주요 책임</h2>

          <div className="space-y-4">
            {jobDetail.positionResponsibilities.map((responsibility, index) => (
              <div key={index} className="flex items-center gap-4">
                <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-gray-200 text-2xl">
                  <Image
                    src={responsibility.imageUrl}
                    width={24}
                    height={24}
                    alt={responsibility.imageUrl}
                  />
                </div>
                <div>
                  <h3 className="mb-1 font-semibold text-neutral-900">
                    {responsibility.name}
                  </h3>
                </div>
              </div>
            ))}
          </div>
        </section>
        <section className="p-4">
          <h2 className="mb-4 text-xl font-bold text-neutral-900">산업 동향</h2>

          <p className="leading-relaxed text-gray-700">
            {jobDetail.industryTrends}
          </p>
        </section>

        <div className="flex w-full items-center justify-center px-4">
          <Link
            className="bg-primary w-full rounded-xl py-3 text-center font-bold"
            href={`/matching?jobId=${id}`}
          >
            멘토 찾기
          </Link>
        </div>
      </PageWithHeader>
    </>
  )
}

export default JobDescriptionPage

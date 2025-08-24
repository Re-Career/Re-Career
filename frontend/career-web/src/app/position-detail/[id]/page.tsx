import React from 'react'
import Image from 'next/image'
import { notFound } from 'next/navigation'
import Header from '@/components/layout/Header'
import Link from 'next/link'
import { getPositionDetail } from '@/services/server/positions'
import PageWithHeader from '@/components/layout/PageWithHeader'

const PositionDescriptionPage = async ({
  params,
}: {
  params: Promise<{ id: string }>
}) => {
  const { id } = await params
  const positionDetail = await getPositionDetail(id)

  if (!positionDetail) {
    notFound()
  }

  return (
    <>
      <Header title="직무설명" showBackButton />
      <PageWithHeader>
        <div className="h-60 w-full flex-shrink-0 overflow-hidden">
          <Image
            src={positionDetail.imageUrl}
            alt={`position_detail_image_${positionDetail.id}`}
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
                  {positionDetail.name}
                </h1>
                <span className="rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-800">
                  {positionDetail.category}
                </span>
              </div>

              <p className="leading-relaxed text-gray-700">
                {positionDetail.description}
              </p>
            </div>
          </div>
        </section>
        <section className="p-4">
          <h2 className="mb-4 text-xl font-bold text-neutral-900">주요 책임</h2>

          <div className="space-y-4">
            {positionDetail.positionResponsibilities.map(
              (responsibility, index) => (
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
              )
            )}
          </div>
        </section>
        <section className="p-4">
          <h2 className="mb-4 text-xl font-bold text-neutral-900">산업 동향</h2>

          <p className="leading-relaxed text-gray-700">
            {positionDetail.industryTrends}
          </p>
        </section>

        <div className="flex w-full items-center justify-center px-4">
          <Link
            className="bg-primary-500 w-full rounded-xl py-3 text-center font-bold"
            href={`/matching?positionIds=${id}`}
          >
            멘토 찾기
          </Link>
        </div>
      </PageWithHeader>
    </>
  )
}

export default PositionDescriptionPage

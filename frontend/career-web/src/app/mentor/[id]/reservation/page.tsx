import Header from '@/components/common/Header'
import { mentorDetails } from '@/mocks/home/mentor-details'
import { redirect } from 'next/navigation'
import Image from 'next/image'
import React from 'react'
import { ReservationForm } from '@/components/mentor/reservation'

const page = async ({ params }: { params: Promise<{ id: string }> }) => {
  const { id } = await params
  const mentor = mentorDetails.find((m) => m.id === parseInt(id))

  if (!mentor) {
    redirect(`/mentor/${id}/profile`)
  }

  return (
    <>
      <Header showBackButton title="멘토와 상담 예약하기" />
      <section className="px-4">
        <h4 className="section-title">멘토 정보</h4>
        <div className="flex gap-4">
          <Image
            src={mentor.profileImage}
            alt={`mentor_reservation_profile_${mentor.id}`}
            width={56}
            height={56}
            className="rounded-full"
          />
          <div>
            <p>{mentor.name}</p>
            <p>{mentor.email}</p>
          </div>
        </div>
      </section>
      <ReservationForm mentorId={id} />
    </>
  )
}

export default page

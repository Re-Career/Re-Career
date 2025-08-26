import { FixedSizeImage } from '@/components/common'
import { Header, PageWithHeader } from '@/components/layout'
import { ReservationForm } from '@/components/mentor/reservation'
import { getMentor } from '@/services/server/mentor'
import { redirect } from 'next/navigation'

const page = async ({ params }: { params: Promise<{ id: string }> }) => {
  const { id } = await params

  const { data: mentor } = await getMentor(id)

  if (!mentor) {
    redirect(`/mentor/${id}/profile`)
  }

  return (
    <>
      <Header showBackButton title="멘토와 상담 예약하기" />
      <PageWithHeader>
        <section>
          <h4 className="section-title">멘토 정보</h4>
          <div className="flex gap-4 px-4">
            <FixedSizeImage
              src={mentor.profileImageUrl}
              alt={`mentor_reservation_profile_${mentor.id}`}
              size="sm"
            />
            <div>
              <p className="font-bold">{mentor.name}</p>
              <p className="text-sm">{mentor.email}</p>
            </div>
          </div>
        </section>
        <ReservationForm mentorId={id} />
      </PageWithHeader>
    </>
  )
}

export default page

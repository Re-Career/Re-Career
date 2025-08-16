import Header from '@/components/common/Header'
import MyMentoringList from '@/components/my-page/mentoring/MyMentoringList'
import { getUserProfile } from '@/services/user'

import Image from 'next/image'
import { redirect } from 'next/navigation'
import { GoPerson } from 'react-icons/go'

const MyPagePage = async () => {
  const data = await getUserProfile()

  if (!data) {
    return
  }

  return (
    <div className="flex h-screen flex-col">
      <Header title="마이페이지" />
      <main className="flex-1">
        <section className="flex flex-col items-center gap-4 p-4">
          <Image
            src={data.profileImageUrl ?? ''}
            alt={`my_page_image_${data.id}`}
            width={128}
            height={128}
            className="rounded-full"
          />
          <p className="text-xl font-bold">{data.name}</p>
        </section>
        <section>
          <h2 className="section-title">내 멘토링</h2>
          <MyMentoringList />
        </section>
        <section>
          <h2 className="section-title">설정</h2>
          <div className="flex items-center gap-4 px-4">
            <div className="w-min rounded-lg bg-gray-100 p-3">
              <GoPerson className="h-6 w-6" />
            </div>
            <p>계정</p>
          </div>
        </section>
      </main>
    </div>
  )
}

export default MyPagePage

import { Header, PageWithHeader } from '@/components/layout'
import { MyMentoringList } from '@/components/my-page'
import { getUserProfile } from '@/services/user'
import Image from 'next/image'
import Link from 'next/link'
import { GoPerson as PersonIcon } from 'react-icons/go'
import { LuPencil as PencilIcon } from 'react-icons/lu'

const MyPagePage = async () => {
  const data = await getUserProfile()

  if (!data) {
    return
  }

  return (
    <>
      <Header title="마이페이지" />
      <PageWithHeader>
        <section className="flex flex-col items-center gap-4 p-4">
          <div className="relative">
            <Image
              src={data.profileImageUrl ?? ''}
              alt={`my_page_image_${data.id}`}
              width={128}
              height={128}
              className="rounded-full"
            />
            <Link
              href="/settings/profile"
              className="absolute right-0 bottom-0 rounded-full bg-white p-2 shadow-lg"
            >
              <PencilIcon className="h-4 w-4 text-gray-600" />
            </Link>
          </div>
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
              <PersonIcon className="h-6 w-6" />
            </div>
            <p>계정</p>
          </div>
        </section>
      </PageWithHeader>
    </>
  )
}

export default MyPagePage

import { Header, PageWithHeader } from '@/components/layout'
import { MyMentoringList } from '@/components/my-page'
import { getUserProfile } from '@/services/user'
import { hasProfileImage } from '@/lib/constants/images'
import Image from 'next/image'
import Link from 'next/link'
import { GoPerson as PersonIcon } from 'react-icons/go'
import { IoCameraOutline } from 'react-icons/io5'
import { LuLogOut as LogoutIcon } from 'react-icons/lu'
import { LogoutButton } from '@/components/my-page/LogoutButton'

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
            {hasProfileImage(data.profileImageUrl) ? (
              <div className="relative h-32 w-32">
                <Image
                  src={data.profileImageUrl!}
                  alt={`my_page_image_${data.id}`}
                  fill
                  className="rounded-full object-cover"
                />
                <Link
                  href="/settings/profile"
                  className="absolute right-0 bottom-0 rounded-full bg-white p-2 shadow-lg"
                >
                  <IoCameraOutline className="h-4 w-4 text-gray-600" />
                </Link>
              </div>
            ) : (
              <Link
                href="/settings/profile"
                className="flex h-32 w-32 items-center justify-center rounded-full bg-gray-200"
              >
                <IoCameraOutline className="h-12 w-12 text-gray-400" />
              </Link>
            )}
          </div>
          <p className="text-xl font-bold">{data.name}</p>
        </section>
        <section>
          <h2 className="section-title">내 멘토링</h2>
          <MyMentoringList />
        </section>
        <section>
          <h2 className="section-title">설정</h2>
          <div className="space-y-4 px-4">
            <Link href="/settings/profile" className="flex items-center gap-4">
              <div className="w-min rounded-lg bg-gray-100 p-3">
                <PersonIcon className="h-6 w-6" />
              </div>
              <p>계정 수정</p>
            </Link>
            <div className="flex items-center gap-4">
              <div className="w-min rounded-lg bg-gray-100 p-3">
                <LogoutIcon className="h-6 w-6" />
              </div>
              <LogoutButton />
            </div>
          </div>
        </section>
      </PageWithHeader>
    </>
  )
}

export default MyPagePage

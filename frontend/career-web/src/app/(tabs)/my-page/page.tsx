import { Header, PageWithHeader } from '@/components/layout'
import { MyMentoringList, ProfileImage } from '@/components/my-page'
import { LogoutButton } from '@/components/my-page/LogoutButton'
import PersonalityTagsSection from '@/components/my-page/PersonalityTagsSection'
import { getPersonalityTags } from '@/services/personality-tags'
import { getUserProfile } from '@/services/user'
import Link from 'next/link'
import { GoPerson as PersonIcon } from 'react-icons/go'
import { LuLogOut as LogoutIcon } from 'react-icons/lu'

const MyPagePage = async () => {
  const user = await getUserProfile()
  const tags = await getPersonalityTags()

  if (!user) {
    return (
      <>
        <Header title="마이페이지" />
        <PageWithHeader>
          <div className="flex h-64 items-center justify-center">
            <p>사용자 정보를 불러올 수 없습니다.</p>
          </div>
        </PageWithHeader>
      </>
    )
  }

  return (
    <>
      <Header title="마이페이지" />
      <PageWithHeader>
        <section className="flex flex-col items-center gap-4 p-4">
          <ProfileImage user={user} />
          <p className="text-xl font-bold">{user.name}</p>
        </section>

        <PersonalityTagsSection
          personalityTags={user.personalityTags || []}
          allTags={tags}
        />

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

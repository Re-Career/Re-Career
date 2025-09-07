import { Header, PageWithHeader } from '@/components/layout'
import { MySessionList, ProfileImage } from '@/components/my-page'
import { LogoutButton } from '@/components/my-page/LogoutButton'
import PersonalityTagsSection from '@/components/my-page/PersonalityTagsSection'
import { getPersonalityTags } from '@/services/server/personality-tags'
import { getUserProfile } from '@/services/server/user'
import Link from 'next/link'
import { GoPerson as PersonIcon } from 'react-icons/go'
import { LuLogOut as LogoutIcon } from 'react-icons/lu'
import { useLoginSheet } from '@/store/useLoginSheet'
import { getCookieValue } from '@/utils/getCookie'

const MyPagePage = async () => {
  const [{ data: user }, { data: tags }] = await Promise.all([
    getUserProfile(),
    getPersonalityTags(),
  ])

  function SettingsLinkWithAuth() {
    const { onOpen } = useLoginSheet()
    const handleClick = (e: React.MouseEvent) => {
      const token = getCookieValue('accessToken')

      if (!token) {
        e.preventDefault()
        onOpen()
      } else {
        window.location.href = '/settings/profile'
      }
    }

    return (
      <Link
        href="/settings/profile"
        className="flex items-center gap-4"
        onClick={handleClick}
      >
        <div className="w-min rounded-lg bg-gray-100 p-3">
          <PersonIcon className="h-6 w-6" />
        </div>
        <p>계정 수정</p>
      </Link>
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
          <MySessionList />
        </section>

        <section>
          <h2 className="section-title">설정</h2>
          <div className="space-y-4 px-4">
            <SettingsLinkWithAuth />
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

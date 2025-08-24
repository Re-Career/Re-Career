import Header from '@/components/layout/Header'
import PageWithHeader from '@/components/layout/PageWithHeader'
import { ProfileEditForm } from '@/components/my-page'
import { getUserProfile } from '@/services/server/user'

const ProfileEditPage = async () => {
  const data = await getUserProfile()

  if (!data) {
    return
  }

  return (
    <>
      <Header title="프로필 수정" />
      <PageWithHeader>
        <ProfileEditForm userData={data} />
      </PageWithHeader>
    </>
  )
}

export default ProfileEditPage

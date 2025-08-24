import { Header, PageWithHeader } from '@/components/layout'
import { SignUpForm } from '@/components/sign-up'
import { getCities, getProvinces } from '@/services/server/locations'
import { getPersonalityTags } from '@/services/server/personality-tags'
import { RoleType } from '@/types/global'

const SignUpPage = async ({
  params,
}: {
  params: Promise<{ role: string }>
}) => {
  const { role } = await params
  const [tags, provinces, cities] = await Promise.all([
    getPersonalityTags(),
    getProvinces(),
    getCities(),
  ])

  const _role = role.toLocaleUpperCase() as RoleType

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <PageWithHeader>
        <SignUpForm
          role={_role}
          tags={tags}
          provinces={provinces}
          cities={cities}
        />
      </PageWithHeader>
    </>
  )
}

export default SignUpPage

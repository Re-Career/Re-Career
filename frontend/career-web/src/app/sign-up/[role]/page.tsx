import { Header, PageWithHeader } from '@/components/layout'
import { SignUpForm } from '@/components/sign-up'
import { getPersonalityTags } from '@/services/personality-tags'
import { RoleType } from '@/types/global'

const SignUpPage = async ({
  params,
}: {
  params: Promise<{ role: string }>
}) => {
  const { role } = await params
  const tags = await getPersonalityTags()
  const _role = role.toLocaleUpperCase() as RoleType

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <PageWithHeader>
        <SignUpForm role={_role} tags={tags} />
      </PageWithHeader>
    </>
  )
}

export default SignUpPage

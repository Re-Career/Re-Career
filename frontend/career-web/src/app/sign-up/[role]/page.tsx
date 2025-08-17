import Header from '@/components/common/Header'
import SignUpForm from '@/components/sign-up/SignUpForm'
import PageWithHeader from '@/components/ui/PageWithHeader'
import { ROLE_TYPES } from '@/lib/constants/global'
import { getPersonalityTags } from '@/services/personality-tags'
import { RoleType } from '@/types/global'
import { redirect } from 'next/navigation'

const SignUpPage = async ({
  params,
}: {
  params: Promise<{ role: string }>
}) => {
  const { role } = await params

  const _role = role.toLocaleUpperCase() as RoleType

  const tags = await getPersonalityTags()

  if (!Object.values(ROLE_TYPES).includes(_role)) {
    redirect('/')
  }

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

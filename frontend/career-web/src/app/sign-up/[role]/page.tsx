import Header from '@/components/common/Header'
import SignUpForm from '@/components/sign-up/SignUpForm'
import { RoleTypes } from '@/lib/constants/global'
import { getPersonalityTags } from '@/services/personality-tags'
import { RoleType } from '@/types/global'
import { redirect } from 'next/navigation'

const SignUpPage = async ({
  params,
}: {
  params: Promise<{ role: RoleType }>
}) => {
  const { role } = await params

  const tags = await getPersonalityTags()

  if (!Object.values(RoleTypes).includes(role)) {
    redirect('/')
  }

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <SignUpForm role={role} tags={tags} />
    </>
  )
}

export default SignUpPage

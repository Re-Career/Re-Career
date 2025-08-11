import Header from '@/components/common/Header'
import SignUpForm from '@/components/sign-up/SignUpForm'
import { RoleTypes } from '@/lib/constants/global'
import { RoleType } from '@/types/global'
import { redirect } from 'next/navigation'

const SignUpPage = async ({
  params,
}: {
  params: Promise<{ role: RoleType }>
}) => {
  const { role } = await params

  if (!Object.values(RoleTypes).includes(role)) {
    redirect('/')
  }

  return (
    <>
      <Header title="회원가입" showCancelButton />
      <SignUpForm role={role} />
    </>
  )
}

export default SignUpPage

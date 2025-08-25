import {
  ProvincePositionList,
  TrendPositionList,
  MentorList,
  IndustryNews,
} from '@/components/home'
import { Header, PageWithHeader } from '@/components/layout'
import { getCookie } from '../actions/global/action'

const HomePage = async () => {
  const cachedLocation = await getCookie('location')

  return (
    <>
      <Header title="Re:Career" />
      <PageWithHeader>
        <main className="flex flex-col gap-4">
          <TrendPositionList />
          <ProvincePositionList cachedLocation={cachedLocation} />
          <MentorList />
          <IndustryNews />
        </main>
      </PageWithHeader>
    </>
  )
}

export default HomePage

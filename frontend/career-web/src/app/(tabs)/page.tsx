import {
  ReginalJobList,
  TrendJobList,
  MentorList,
  IndustryNews,
} from '@/components/home'
import { Header, PageWithHeader } from '@/components/layout'

const HomePage = async () => {
  return (
    <>
      <Header title="Re:Career" />
      <PageWithHeader>
        <main className="flex-1">
          <TrendJobList />
          <ReginalJobList />
          <MentorList />
          <IndustryNews />
        </main>
      </PageWithHeader>
    </>
  )
}

export default HomePage

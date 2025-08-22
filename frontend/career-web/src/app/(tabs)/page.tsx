import {
  ReginalPositionList,
  TrendPositionList,
  MentorList,
  IndustryNews,
} from '@/components/home'
import { Header, PageWithHeader } from '@/components/layout'

const HomePage = async () => {
  return (
    <>
      <Header title="Re:Career" />
      <PageWithHeader>
        <main className="flex flex-col gap-4">
          <TrendPositionList />
          <ReginalPositionList />
          <MentorList />
          <IndustryNews />
        </main>
      </PageWithHeader>
    </>
  )
}

export default HomePage

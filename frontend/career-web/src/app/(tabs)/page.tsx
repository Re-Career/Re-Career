import Header from '@/components/common/Header'
import {
  ReginalJobList,
  TrendJobList,
  MentorList,
  IndustryNews,
} from '@/components/home'
import PageWithHeader from '@/components/ui/PageWithHeader'

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

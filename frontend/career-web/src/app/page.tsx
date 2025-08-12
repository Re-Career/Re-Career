import Header from '@/components/common/Header'
import {
  ReginalJobList,
  TrendJobList,
  MentorList,
  IndustryNews,
} from '@/components/home'

const HomePage = async () => {
  return (
    <>
      <Header title="Re:Career" />
      <main className="flex-1">
        <TrendJobList />
        <ReginalJobList />
        <MentorList />
        <IndustryNews />
      </main>
    </>
  )
}
export default HomePage

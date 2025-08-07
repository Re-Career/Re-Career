import Header from '@/components/common/Header'
import { TrendJobList, ReginalJobList } from '@/components/home'
import MentorList from '@/components/home/MentorList'
import IndustryNews from '@/components/home/IndustryNews'

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

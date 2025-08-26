import {
  TrendPositionList,
  IndustryNews,
  RelatedLocationGroup,
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
          <RelatedLocationGroup cachedLocation={cachedLocation} />
          <IndustryNews />
        </main>
      </PageWithHeader>
    </>
  )
}

export default HomePage

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
      <PageWithHeader className="space-y-4">
        <TrendPositionList />
        <RelatedLocationGroup cachedLocation={cachedLocation} />
        <IndustryNews />
      </PageWithHeader>
    </>
  )
}

export default HomePage

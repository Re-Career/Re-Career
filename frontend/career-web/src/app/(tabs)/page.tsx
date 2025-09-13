import {
  TrendPositionList,
  IndustryNews,
  RelatedLocationGroup,
} from '@/components/home'
import { Header, PageWithHeader } from '@/components/layout'
import { getCookie } from '../actions/global/action'
import { getPositionsByProvince } from '@/services/server/positions'
import { getMentors } from '@/services/server/mentor'

const DEFAULT_PROVINCE_ID = 1

const HomePage = async () => {
  const cachedLocation = await getCookie('location')
  const { data: defaultTrendPositions } = await getPositionsByProvince({
    provinceId: DEFAULT_PROVINCE_ID,
  })
  const { data: defaultMentors } = await getMentors(DEFAULT_PROVINCE_ID)

  return (
    <>
      <Header title="Re:Career" />
      <PageWithHeader className="space-y-4">
        <TrendPositionList />
        <RelatedLocationGroup
          cachedLocation={cachedLocation}
          defaultTrendPositions={defaultTrendPositions}
          defaultMentors={defaultMentors}
        />
        <IndustryNews />
      </PageWithHeader>
    </>
  )
}

export default HomePage

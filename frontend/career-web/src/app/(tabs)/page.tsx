import {
  ProvincePositionList,
  TrendPositionList,
  MentorList,
  IndustryNews,
} from '@/components/home'
import { Header, PageWithHeader } from '@/components/layout'
import { getPositionsByProvince } from '@/services/server/positions'
import { getUserProfile } from '@/services/server/user'

const HomePage = async () => {
  const user = await getUserProfile()
  const params = user.cityId
    ? { provinceId: user.provinceId, cityId: user.cityId }
    : { provinceId: user.provinceId ?? 1 }

  const positionsByProvince = await getPositionsByProvince(params)

  return (
    <>
      <Header title="Re:Career" />
      <PageWithHeader>
        <main className="flex flex-col gap-4">
          <TrendPositionList />
          <ProvincePositionList positionsByProvince={positionsByProvince} />
          <MentorList />
          <IndustryNews />
        </main>
      </PageWithHeader>
    </>
  )
}

export default HomePage

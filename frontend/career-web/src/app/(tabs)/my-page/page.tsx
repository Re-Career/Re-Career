import { getToken } from '@/app/actions/auth/action'
import Header from '@/components/common/Header'
import MyMentoringList from '@/components/my-page/mentoring/MyMentoringList'
import { sendMessageToNative } from '@/utils/webview'
import Image from 'next/image'
import { GoPerson } from 'react-icons/go'

const MyPagePage = async () => {
  const data = {
    id: 1,
    name: '김지연',
    job: '시니어 소프트웨어 엔지니어',
    email: 'sophia.kim@example.com',
    profileImage:
      'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face',
  }

  return (
    <div className="flex h-screen flex-col">
      <Header title="마이페이지" />
      <main className="flex-1">
        <section className="flex flex-col items-center gap-4 p-4">
          <Image
            src={data.profileImage}
            alt={`my_page_image_${data.id}`}
            width={128}
            height={128}
            className="rounded-full"
          />
          <p className="text-xl font-bold">{data.name}</p>
        </section>
        <section>
          <h2 className="section-title">내 멘토링</h2>
          <MyMentoringList />
        </section>
        <section>
          <h2 className="section-title">설정</h2>
          <div className="flex items-center gap-4 px-4">
            <div className="w-min rounded-lg bg-gray-100 p-3">
              <GoPerson className="h-6 w-6" />
            </div>
            <p>계정</p>
          </div>
        </section>
      </main>
    </div>
  )
}

export default MyPagePage

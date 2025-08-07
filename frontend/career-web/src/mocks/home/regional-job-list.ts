import { RegionJobs } from '@/types/home'

export const regionalJobs: RegionJobs = {
  region: '서울',
  jobs: [
    {
      id: 1,
      name: '소프트웨어 엔지니어',
      category: '기술',
      illustration:
        'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=80&h=80&fit=crop&crop=center',
      rank: 1,
    },
    {
      id: 2,
      name: '데이터 분석가',
      category: '데이터',
      illustration:
        'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=80&h=80&fit=crop&crop=center',
      rank: 2,
    },
    {
      id: 3,
      name: '프로덕트 매니저',
      category: '제품',
      illustration:
        'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=80&h=80&fit=crop&crop=center',
      rank: 3,
    },
  ],
}

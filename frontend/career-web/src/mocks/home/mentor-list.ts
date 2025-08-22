import { Mentor } from '@/types/mentor'

export const mentors: Mentor[] = [
  {
    id: 1,
    name: '김지연',
    position: { id: 1, name: '시니어 소프트웨어 엔지니어' },
    email: 'jiyeon.kim@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=120&h=120&fit=crop&crop=face',
    company: { id: 1, name: '네이버' },
    experience: 8,
    region: { id: 1, name: '서울' },
    meetingType: 'online',
    personalityTags: [
      { id: 1, name: '친근한' },
      { id: 2, name: '체계적인' },
    ],
  },
  {
    id: 2,
    name: '박서영',
    position: { id: 2, name: '프로덕트 매니저' },
    email: 'seoyoung.park@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=120&h=120&fit=crop&crop=face',
    company: { id: 2, name: '토스' },
    experience: 6,
    region: { id: 2, name: '서울' },
    meetingType: 'offline',
    personalityTags: [
      { id: 3, name: '논리적인' },
      { id: 4, name: '소통능력' },
    ],
  },
  {
    id: 3,
    name: '이서연',
    position: { id: 3, name: 'UX/UI 디자이너' },
    email: 'seoyeon.lee@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120&h=120&fit=crop&crop=face',
    company: { id: 3, name: '당근마켓' },
    experience: 5,
    region: { id: 3, name: '부산' },
    meetingType: null,
    personalityTags: [
      { id: 5, name: '창의적인' },
      { id: 6, name: '섬세한' },
    ],
  },
  {
    id: 4,
    name: '정수민',
    position: { id: 4, name: '데이터 사이언티스트' },
    email: 'sumin.jung@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=120&h=120&fit=crop&crop=face',
    company: { id: 4, name: 'LG AI연구원' },
    experience: 4,
    region: { id: 4, name: '대전' },
    meetingType: 'online',
    personalityTags: [
      { id: 7, name: '분석적인' },
      { id: 8, name: '꼼꼼한' },
    ],
  },
  {
    id: 5,
    name: '최예린',
    position: { id: 5, name: '마케팅 매니저' },
    email: 'yerin.choi@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=120&h=120&fit=crop&crop=face',
    company: { id: 5, name: '쿠팡' },
    experience: 7,
    region: { id: 5, name: '서울' },
    meetingType: 'offline',
    personalityTags: [
      { id: 9, name: '적극적인' },
      { id: 10, name: '트렌드 민감' },
    ],
  },
]

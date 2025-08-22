import { Mentor } from '@/types/home'

export const searchMentors: Mentor[] = [
  {
    id: 1,
    name: '김지연',
    position: '시니어 소프트웨어 엔지니어',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=120&h=120&fit=crop&crop=face',
    company: '카카오',
    experience: 8,
    region: '서울',
    meetingType: 'both',
    personalityTags: [
      { id: 1, name: '친근함' },
      { id: 2, name: '꼼꼼함' },
    ],
  },
  {
    id: 2,
    name: '박서영',
    position: '프로덕트 매니저',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=120&h=120&fit=crop&crop=face',
    company: '네이버',
    experience: 6,
    region: '서울',
    meetingType: 'online',
    personalityTags: [
      { id: 3, name: '논리적' },
      { id: 4, name: '체계적' },
    ],
  },
  {
    id: 3,
    name: '이서연',
    position: 'UX/UI 디자이너',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120&h=120&fit=crop&crop=face',
    company: '당근마켓',
    experience: 5,
    region: '서울',
    meetingType: 'offline',
    personalityTags: [
      { id: 5, name: '창의적' },
      { id: 6, name: '소통능력우수' },
    ],
  },
  {
    id: 4,
    name: '정수민',
    position: '데이터 사이언티스트',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=120&h=120&fit=crop&crop=face',
    company: '토스',
    experience: 4,
    region: '서울',
    meetingType: 'both',
    personalityTags: [
      { id: 7, name: '분석적' },
      { id: 8, name: '차분함' },
    ],
  },
  {
    id: 5,
    name: '최예린',
    position: '마케팅 매니저',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=120&h=120&fit=crop&crop=face',
    company: '배달의민족',
    experience: 7,
    region: '부산',
    meetingType: 'online',
    personalityTags: [
      { id: 9, name: '활발함' },
      { id: 10, name: '열정적' },
    ],
  },
  {
    id: 6,
    name: '강수현',
    position: '스타트업 CEO',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=120&h=120&fit=crop&crop=face',
    company: '자체 창업',
    experience: 10,
    region: '대구',
    meetingType: 'both',
    personalityTags: [
      { id: 11, name: '리더십' },
      { id: 12, name: '격려잘함' },
    ],
  },
  {
    id: 7,
    name: '윤하영',
    position: '백엔드 개발자',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=120&h=120&fit=crop&crop=face',
    company: '라인',
    experience: 5,
    region: '서울',
    meetingType: 'online',
    personalityTags: [
      { id: 8, name: '차분함' },
      { id: 13, name: '설명잘함' },
    ],
  },
  {
    id: 8,
    name: '송미영',
    position: '프론트엔드 개발자',
    email: 'example@gmail.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=120&h=120&fit=crop&crop=face',
    company: '쿠팡',
    experience: 6,
    region: '서울',
    meetingType: 'both',
    personalityTags: [
      { id: 2, name: '꼼꼼함' },
      { id: 14, name: '인내심강함' },
    ],
  },
]

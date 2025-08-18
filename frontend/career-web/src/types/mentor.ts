export interface PersonalityTag {
  id: number
  name: string
}

export interface Mentor {
  id: number
  name: string
  job: {
    id: number
    name: string
  }
  email: string
  profileImageUrl: string
  company: {
    id: number
    name: string
  }
  experience: number
  region: {
    id: number
    name: string
  }
  meetingType: 'online' | 'offline' | null
  personalityTags?: PersonalityTag[]
}

export interface MentorDetail extends Mentor {
  shortDescription?: string
  introduction?: string
  skills?: string[]
  career?: string[]
  feedback?: {
    rating: number
    count: number
    comments: {
      id: number
      user: string
      rating: number
      comment: string
      date: string
    }[]
  }
}

export type FilterOptions = Record<string, string[]>

export interface FilterConfig {
  key: string
  title: string
  options: FilterOptions[]
}

export const mockMentors: Mentor[] = [
  {
    id: 1,
    name: '김민수',
    job: { id: 1, name: '프론트엔드 개발자' },
    email: 'minsu.kim@example.com',
    profileImageUrl: 'https://example.com/profile1.jpg',
    company: { id: 1, name: '네이버' },
    experience: 5,
    region: { id: 1, name: '서울' },
    meetingType: 'online',
    personalityTags: [
      { id: 1, name: '친근한' },
      { id: 2, name: '꼼꼼한' },
    ],
  },
  {
    id: 2,
    name: '이소영',
    job: { id: 2, name: '백엔드 개발자' },
    email: 'soyoung.lee@example.com',
    profileImageUrl: 'https://example.com/profile2.jpg',
    company: { id: 2, name: '카카오' },
    experience: 7,
    region: { id: 2, name: '부산' },
    meetingType: 'offline',
    personalityTags: [
      { id: 3, name: '논리적인' },
      { id: 4, name: '차분한' },
    ],
  },
  {
    id: 3,
    name: '박준혁',
    job: { id: 3, name: '풀스택 개발자' },
    email: 'junhyuk.park@example.com',
    profileImageUrl: 'https://example.com/profile3.jpg',
    company: { id: 3, name: '토스' },
    experience: 3,
    region: { id: 3, name: '인천' },
    meetingType: null,
    personalityTags: [
      { id: 5, name: '적극적인' },
      { id: 6, name: '창의적인' },
    ],
  },
  {
    id: 4,
    name: '최유진',
    job: { id: 4, name: '데이터 사이언티스트' },
    email: 'yujin.choi@example.com',
    profileImageUrl: 'https://example.com/profile4.jpg',
    company: { id: 4, name: '배달의민족' },
    experience: 4,
    region: { id: 4, name: '대구' },
    meetingType: 'online',
  },
  {
    id: 5,
    name: '정태현',
    job: { id: 5, name: 'DevOps 엔지니어' },
    email: 'taehyun.jung@example.com',
    profileImageUrl: 'https://example.com/profile5.jpg',
    company: { id: 5, name: '쿠팡' },
    experience: 6,
    region: { id: 5, name: '광주' },
    meetingType: 'offline',
    personalityTags: [
      { id: 7, name: '신중한' },
      { id: 8, name: '체계적인' },
    ],
  },
]

export const mockMentorDetails: MentorDetail[] = [
  {
    ...mockMentors[0],
    shortDescription: '5년차 프론트엔드 개발자로 React, Vue.js 전문가입니다.',
    introduction:
      '안녕하세요! 네이버에서 프론트엔드 개발을 하고 있는 김민수입니다. 주로 React와 Vue.js를 활용한 웹 애플리케이션 개발에 전문성을 가지고 있으며, 신입 개발자들의 성장을 도와드리는 것을 즐겨합니다.',
    skills: ['React', 'Vue.js', 'TypeScript', 'JavaScript', 'HTML/CSS'],
    career: [
      '네이버 - 프론트엔드 개발자 (2019~현재)',
      '스타트업 A - 웹 개발자 (2017~2019)',
    ],
    feedback: {
      rating: 4.8,
      count: 25,
      comments: [
        {
          id: 1,
          user: '김철수',
          rating: 5,
          comment: '정말 친절하게 설명해주셔서 많은 도움이 되었습니다!',
          date: '2024-01-15',
        },
        {
          id: 2,
          user: '박영희',
          rating: 4,
          comment: '실무 경험을 바탕으로 한 조언이 매우 유익했어요.',
          date: '2024-01-10',
        },
      ],
    },
  },
  {
    ...mockMentors[1],
    shortDescription: '7년차 백엔드 개발자로 Node.js, Python 전문가입니다.',
    introduction:
      '카카오에서 백엔드 개발을 담당하고 있는 이소영입니다. 대용량 트래픽 처리와 시스템 아키텍처 설계에 관심이 많으며, 개발자로 성장하고자 하는 분들을 응원합니다.',
    skills: ['Node.js', 'Python', 'PostgreSQL', 'Redis', 'Docker'],
    career: [
      '카카오 - 백엔드 개발자 (2017~현재)',
      'IT 서비스 회사 B - 서버 개발자 (2015~2017)',
    ],
    feedback: {
      rating: 4.9,
      count: 32,
      comments: [
        {
          id: 3,
          user: '이민호',
          rating: 5,
          comment: '체계적인 설명으로 백엔드 개념을 잘 이해할 수 있었습니다.',
          date: '2024-01-20',
        },
      ],
    },
  },
]

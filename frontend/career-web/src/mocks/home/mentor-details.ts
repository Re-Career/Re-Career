import { MentorDetail } from '@/types/mentor'

export const mentorDetails: MentorDetail[] = [
  {
    id: 1,
    name: '김지연',
    position: { id: 1, name: '시니어 소프트웨어 엔지니어' },
    email: 'sophia.kim@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop&crop=face',
    company: { id: 1, name: '카카오' },
    experience: 8,
    region: { id: 1, name: '서울' },
    meetingType: 'online',
    personalityTags: [
      { id: 1, name: '친근함' },
      { id: 2, name: '꼼꼼함' },
    ],
    shortDescription:
      '카카오에서 8년간 근무하며 백엔드 개발과 시스템 아키텍처 설계를 담당하고 있습니다.',
    introduction:
      '안녕하세요! 저는 카카오에서 시니어 소프트웨어 엔지니어로 일하고 있는 김지연입니다. 8년간의 실무 경험을 바탕으로 주니어 개발자들의 성장을 도와드리고 있습니다. 백엔드 개발부터 시스템 아키텍처 설계까지 다양한 경험을 가지고 있으며, 특히 대용량 트래픽 처리와 마이크로서비스 아키텍처에 관심이 많습니다. 개발자로서의 커리어 고민이나 기술적인 문제 해결에 대해 함께 이야기하고 싶습니다.',
    skills: ['Java', 'Spring Boot', 'Kubernetes', 'AWS', 'MySQL', 'Redis'],
    career: [
      '2023-현재: 카카오 시니어 소프트웨어 엔지니어',
      '2020-2023: 네이버 소프트웨어 엔지니어',
      '2018-2020: 스타트업 백엔드 개발자',
      '2016-2018: 삼성전자 주니어 개발자',
    ],
    feedback: {
      rating: 4.8,
      count: 32,
      comments: [
        {
          id: 1,
          user: '박**',
          rating: 5,
          comment:
            '정말 친절하고 구체적으로 알려주셔서 많은 도움이 되었습니다. 커리어 방향성에 대해 명확한 조언을 받았어요.',
          date: '2024-01-15',
        },
        {
          id: 2,
          user: '이**',
          rating: 5,
          comment:
            '기술적인 부분뿐만 아니라 개발자로서의 마인드셋까지 배울 수 있었습니다. 추천합니다!',
          date: '2024-01-10',
        },
        {
          id: 3,
          user: '최**',
          rating: 4,
          comment:
            '실무 경험을 바탕으로 한 조언이 매우 실질적이었습니다. 감사합니다.',
          date: '2024-01-05',
        },
      ],
    },
  },
  {
    id: 2,
    name: '박서영',
    position: { id: 2, name: '프로덕트 매니저' },
    email: 'sophia.park@example.com',
    profileImageUrl:
      'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=200&h=200&fit=crop&crop=face',
    company: { id: 2, name: '네이버' },
    experience: 6,
    region: { id: 2, name: '서울' },
    meetingType: 'online',
    personalityTags: [
      { id: 3, name: '논리적' },
      { id: 4, name: '체계적' },
    ],
    shortDescription:
      '네이버에서 6년간 다양한 프로덕트의 기획과 운영을 담당하고 있습니다.',
    introduction:
      '안녕하세요! 네이버에서 프로덕트 매니저로 일하고 있는 박서영입니다. 6년간 다양한 프로덕트의 기획부터 런칭, 운영까지 전 과정을 경험했습니다. 데이터 기반의 의사결정과 사용자 중심의 제품 개발에 관심이 많으며, PM이 되고 싶은 분들이나 현재 PM으로 일하고 계신 분들의 고민을 함께 나누고 싶습니다.',
    skills: [
      'Product Strategy',
      'Data Analysis',
      'Figma',
      'SQL',
      'A/B Testing',
      'Agile',
    ],
    career: [
      '2022-현재: 네이버 시니어 프로덕트 매니저',
      '2019-2022: 카카오 프로덕트 매니저',
      '2018-2019: 스타트업 프로덕트 기획자',
    ],
    feedback: {
      rating: 4.9,
      count: 28,
      comments: [
        {
          id: 1,
          user: '김**',
          rating: 5,
          comment:
            'PM이 되기 위한 구체적인 로드맵을 제시해주셔서 정말 도움이 되었습니다.',
          date: '2024-01-18',
        },
        {
          id: 2,
          user: '정**',
          rating: 5,
          comment:
            '논리적이고 체계적인 설명으로 이해하기 쉬웠습니다. 감사해요!',
          date: '2024-01-12',
        },
      ],
    },
  },
]

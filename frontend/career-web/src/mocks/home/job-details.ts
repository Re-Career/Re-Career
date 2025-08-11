import { JobDetail } from '@/types/home'

export const jobDetails: JobDetail[] = [
  {
    id: 1,
    name: '소프트웨어 엔지니어',
    category: '기술',
    description:
      '소프트웨어 개발의 전 과정을 담당하며, 사용자의 니즈를 충족하는 고품질 소프트웨어를 설계하고 구현하는 전문가입니다. 프로그래밍 언어를 활용해 애플리케이션, 시스템, 플랫폼을 개발하고 유지보수합니다.',
    famousPerson: {
      name: '김지연',
      image:
        'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&h=400&fit=crop&crop=face',
      title: '카카오 개발자 출신 스타트업 CEO',
    },
    responsibilities: [
      {
        icon: '💻',
        title: '코드 개발',
        description: '요구사항에 맞는 소프트웨어 코드 작성',
      },
      {
        icon: '🔍',
        title: '시스템 분석',
        description: '기존 시스템 분석 및 개선사항 도출',
      },
      {
        icon: '🧪',
        title: '테스팅',
        description: '소프트웨어 품질 보장을 위한 테스트',
      },
      {
        icon: '🔧',
        title: '유지보수',
        description: '운영 중인 시스템의 버그 수정 및 개선',
      },
      {
        icon: '📋',
        title: '문서화',
        description: '개발 과정 및 결과물 문서 작성',
      },
    ],
    industryTrends: [
      '클라우드 네이티브 개발이 주요 트렌드로 부상하며 Kubernetes, Docker 기술 수요 증가',
      'AI/ML 통합 개발로 TensorFlow, PyTorch 등 머신러닝 프레임워크 활용 능력 필수',
      'DevOps 문화 확산으로 CI/CD, 자동화 도구 경험이 중요한 채용 조건으로 대두',
      'MSA(마이크로서비스) 아키텍처 도입으로 분산 시스템 설계 역량 요구 증가',
      '보안이 강화되면서 시큐어 코딩, DevSecOps 지식이 필수 스킬로 자리잡음',
    ],
  },
  {
    id: 2,
    name: '데이터 분석가',
    category: '데이터',
    description:
      '방대한 데이터에서 의미 있는 인사이트를 도출하여 비즈니스 의사결정을 지원하는 전문가입니다. 통계학, 수학적 지식과 프로그래밍 스킬을 활용해 데이터를 수집, 처리, 분석합니다.',
    famousPerson: {
      name: '이수연',
      image:
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&h=400&fit=crop&crop=face',
      title: '네이버 AI 연구소 수석연구원',
    },
    responsibilities: [
      {
        icon: '📊',
        title: '데이터 수집',
        description: '다양한 소스에서 필요한 데이터 수집 및 정제',
      },
      {
        icon: '🔢',
        title: '통계 분석',
        description: '통계적 방법을 활용한 데이터 패턴 분석',
      },
      {
        icon: '📈',
        title: '시각화',
        description: '분석 결과를 이해하기 쉬운 차트와 그래프로 표현',
      },
      {
        icon: '💡',
        title: '인사이트 도출',
        description: '데이터에서 비즈니스 가치 있는 통찰 발견',
      },
      {
        icon: '📝',
        title: '보고서 작성',
        description: '분석 결과를 명확한 보고서로 작성 및 발표',
      },
    ],
    industryTrends: [
      '빅데이터 처리를 위한 Spark, Hadoop 등 분산처리 기술 수요 급증',
      '실시간 데이터 분석 니즈 증가로 스트리밍 데이터 처리 기술 중요도 상승',
      'AutoML 도구 활용으로 비개발자도 머신러닝 모델 구축 가능한 환경 조성',
      '데이터 거버넌스 강화로 데이터 품질 관리 및 컴플라이언스 역량 필수',
      '클라우드 기반 분석 플랫폼(AWS, GCP, Azure) 활용 능력이 핵심 스킬로 부상',
    ],
  },
  {
    id: 3,
    name: '프로덕트 매니저',
    category: '제품',
    description:
      '제품의 전체 라이프사이클을 관리하며, 시장 요구사항과 비즈니스 목표를 제품 개발에 반영하는 전략적 리더입니다. 고객 니즈 파악부터 제품 출시까지 전 과정을 조율합니다.',
    famousPerson: {
      name: '박민정',
      image:
        'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop&crop=face',
      title: '배달의민족 CPO (Chief Product Officer)',
    },
    responsibilities: [
      {
        icon: '🎯',
        title: '제품 전략',
        description: '시장 분석을 통한 제품 로드맵 수립',
      },
      {
        icon: '👥',
        title: '팀 협업',
        description: '개발, 디자인, 마케팅 팀 간 조율',
      },
      {
        icon: '📋',
        title: '요구사항 정의',
        description: '고객 니즈를 제품 기능으로 변환',
      },
      {
        icon: '📊',
        title: '성과 분석',
        description: '제품 KPI 설정 및 성과 측정',
      },
      {
        icon: '🚀',
        title: '출시 관리',
        description: '제품 런칭 및 마케팅 전략 수립',
      },
    ],
    industryTrends: [
      '데이터 드리븐 의사결정이 핵심으로, 분석 도구 활용 능력 필수',
      'AI 제품 개발 증가로 머신러닝에 대한 기본 이해 요구 증가',
      '애자일, 스크럼 방법론이 표준이 되어 관련 자격증 보유자 우대',
      '고객 경험(UX) 중심 제품 개발로 디자인 씽킹 역량 중요도 상승',
      '글로벌 시장 진출로 다문화 이해 및 현지화 경험이 경쟁력으로 작용',
    ],
  },
]

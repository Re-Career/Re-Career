# Re:Career

> 경력 단절 여성들을 위한 직무 추천과 멘토링을 돕는 플랫폼

🌐 **Live Demo**: https://re-career.vercel.app

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [팀 T-Lions](#-팀-t-lions)
- [기술 스택](#️-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [프로젝트 구조](#-프로젝트-구조)

## 🎯 프로젝트 소개

### Re:Career란?

Re:Career는 **경력 단절을 경험한 여성들**이 성공적으로 직장에 복귀할 수 있도록 돕는 종합 플랫폼입니다.

### 🌟 핵심 기능

- **📍 지역 기반 직무 추천**: 사용자 위치에 맞는 맞춤형 일자리 정보
- **📈 트렌드 분석**: 최신 인기 직업 TOP 20 및 산업 동향
- **👥 전문가 멘토링**: 1:1 개인 상담 및 커리어 코칭
- **🏷️ 개인화 서비스**: 성격 태그 기반 맞춤 추천
- **📰 업계 뉴스**: 최신 취업 정보 및 트렌드

### 🎯 타겟 사용자

- 육아휴직 후 복직을 준비하는 여성
- 경력 전환을 고민하는 여성
- 장기간 경력 단절 후 재취업을 희망하는 여성
- 새로운 분야로의 커리어 체인지를 원하는 여성

## 👥 팀 T-Lions

### 🦁 팀 소개

**T-Lions**는 경력 단절 여성들의 성공적인 사회 복귀를 돕기 위해 결성된 개발팀입니다.

### 👩‍💻 팀원 구성

| 역할                   | 이름   | 담당 영역                         | GitHub                                       |
| ---------------------- | ------ | --------------------------------- | -------------------------------------------- |
| **Backend Developer**  | 이예신 | 서버 개발, API 설계, 데이터베이스 | [@yeslee-v](https://github.com/username)     |
| **Backend Developer**  | 최아연 | 서버 개발, 인증/보안, 배포        | [@ayeon-Choi](https://github.com/username)   |
| **Frontend Developer** | 최수임 | 웹/앱 개발, UI/UX, 사용자 경험    | [@leechoiswim1](https://github.com/username) |

## 🛠️ 기술 스택

### 🖥️ Backend

```
언어: Java 21
프레임워크: Spring Boot 3.5.3
데이터베이스: MySQL, H2 (개발용)
인증: JWT (jjwt 0.12.3), OAuth 2.0 (Kakao)
보안: Spring Security
파일 저장: AWS S3
API 문서: SpringDoc OpenAPI 3 (Swagger)
빌드 도구: Gradle
```

### 🎨 Frontend Web

```
언어: TypeScript
프레임워크: Next.js 15.4.5 (App Router)
React: 19.1.1
스타일링: Tailwind CSS 4
상태관리: Zustand 5.0.8
빌드: Turbopack
배포: Vercel
```

### 🔧 개발 도구

```
버전 관리: Git, GitHub
패키지 매니저: pnpm (workspace)
코드 품질: ESLint 9, Prettier 3
타입 체크: TypeScript 5.8.3
Git Hook: Husky + lint-staged
디자인: Figma
```

## 🏗️ 플로우 차트

![Re:Career 시스템 아키텍처](https://github.com/Re-Career/Re-Career/blob/main/diagram.png?raw=true)

## 📁 프로젝트 구조

```
Re-Career/
├── 🌐 frontend/
│   └── career-web/              # Next.js 웹 애플리케이션
│       ├── src/
│       │   ├── app/            # App Router 페이지
│       │   ├── components/     # 재사용 컴포넌트
│       │   ├── store/          # Zustand 상태 관리
│       │   ├── services/       # API 서비스
│       │   └── utils/          # 유틸리티 함수
│       └── package.json
├── 🖥️ backend/                  # Spring Boot 서버
│   ├── src/main/java/
│   │   └── com/recareer/backend/
│   │       ├── auth/          # 인증 관련
│   │       ├── user/          # 사용자 관리
│   │       ├── mentor/        # 멘토 시스템
│   │       ├── position/      # 직무 정보
│   │       └── config/        # 설정 파일
│   ├── src/main/resources/
│   └── build.gradle
└── 📄 README.md                # 프로젝트 문서
```

## 🚀 시작하기

### 📋 요구사항

- Node.js 18+
- Java 17+
- MySQL 8.0+
- pnpm

### 🛠️ 설치 및 실행

#### Backend 실행

```bash
cd backend
./gradlew bootRun
```

#### Frontend 실행

```bash
# 웹 애플리케이션
cd frontend/career-web
pnpm install
pnpm dev
```

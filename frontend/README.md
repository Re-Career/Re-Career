# Re-Career Frontend

Next.js 웹 애플리케이션과 React Native 모바일 애플리케이션을 포함한 모노레포입니다.

## 사용 가능한 스크립트

### 개발
- `pnpm dev` - 모든 개발 서버 시작
- `pnpm dev:web` - Next.js 개발 서버 시작
- `pnpm dev:native` - Expo 개발 서버 시작

### 빌드
- `pnpm build:web` - Next.js 웹 애플리케이션 프로덕션 빌드

### 프로덕션
- `pnpm start:web` - Next.js 프로덕션 서버 시작
- `pnpm start:native` - Expo 프로덕션 서버 시작

### React Native/Expo
- `pnpm android` - Android 에뮬레이터/디바이스에서 앱 실행
- `pnpm ios` - iOS 시뮬레이터/디바이스에서 앱 실행
- `pnpm native:web` - 웹 브라우저에서 Expo 앱 실행
- `pnpm reset-project` - Expo 프로젝트를 템플릿으로 리셋

### 코드 품질
- `pnpm lint` - 모든 프로젝트에서 ESLint 실행
- `pnpm lint:fix` - 모든 프로젝트에서 ESLint 자동 수정 실행
- `pnpm lint:web` - 웹 프로젝트에서만 ESLint 실행
- `pnpm lint:native` - 네이티브 프로젝트에서만 ESLint 실행

### 포맷팅
- `pnpm format` - Prettier로 모든 파일 포맷팅
- `pnpm format:check` - 파일이 올바르게 포맷팅되었는지 확인

### 유틸리티
- `pnpm clean` - 모든 node_modules와 캐시 정리
- `pnpm prepare` - husky git 훅 설정

## 프로젝트 구조

```
frontend/
├── career-web/          # Next.js 웹 애플리케이션
├── career-native/       # React Native/Expo 모바일 앱
├── package.json         # 공유 의존성이 포함된 루트 package.json
├── eslint.config.js     # 공유 ESLint 설정
└── README.md           # 이 파일
```

## 시작하기

1. 의존성 설치:
   ```bash
   pnpm install
   ```

2. 개발 서버 시작:
   ```bash
   # 모든 프로젝트 시작
   pnpm dev
   
   # 또는 개별 프로젝트 시작
   pnpm dev:web
   pnpm dev:native
   ```

## 기술 스택

### 웹 (career-web)
- Next.js 15
- React 19
- TypeScript
- TailwindCSS

### 모바일 (career-native)
- React Native
- Expo
- TypeScript
- Expo Router

### 공유 도구
- ESLint
- Prettier
- Husky
- lint-staged
- pnpm workspaces
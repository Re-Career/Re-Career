import type { Metadata } from 'next'
import { Noto_Sans, Noto_Sans_KR } from 'next/font/google'
import './globals.css'

const notoSans = Noto_Sans({
  variable: '--font-noto-sans',
  subsets: ['latin'],
  weight: ['400', '500', '600', '700'],
  display: 'swap',
})

const notoSansKR = Noto_Sans_KR({
  variable: '--font-noto-sans-kr',
  subsets: ['latin'],
  weight: ['400', '500', '600', '700'],
  display: 'swap',
})

export const metadata: Metadata = {
  title: 'Re:Career',
  description: '일을 넘어 삶을 설계하는 진짜 커리어 탐색',
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ko">
      <body
        className={`${notoSans.variable} ${notoSansKR.variable} mx-auto flex min-h-screen w-full max-w-[450px] flex-col bg-neutral-50`}
      >
        <div className="flex-1 bg-white">{children}</div>
        <div className="h-4 bg-white" />
      </body>
    </html>
  )
}

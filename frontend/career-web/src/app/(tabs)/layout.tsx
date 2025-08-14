import Tabs from '@/components/common/Tabs'

export default function TabLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <>
      <div className="pb-24">
        {children}
        <Tabs />
      </div>
    </>
  )
}

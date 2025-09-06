import { ReactNode } from 'react'

const PageWithHeader = ({
  className,
  children,
}: {
  className?: string
  children: ReactNode
}) => {
  return (
    <main className={`mt-14 flex-1 ${className ? className : ''}`}>
      {children}
    </main>
  )
}

export default PageWithHeader

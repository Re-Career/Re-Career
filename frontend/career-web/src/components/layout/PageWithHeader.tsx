import { ReactNode } from 'react'

const PageWithHeader = ({
  className,
  children,
}: {
  className?: string
  children: ReactNode
}) => {
  return (
    <div className={`mt-14 flex-1 ${className ? className : ''}`}>
      {children}
    </div>
  )
}

export default PageWithHeader

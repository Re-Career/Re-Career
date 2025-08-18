import { ReactNode } from 'react'

const PageWithHeader = ({ children }: { children: ReactNode }) => {
  return <div className="mt-14 flex-1">{children}</div>
}

export default PageWithHeader

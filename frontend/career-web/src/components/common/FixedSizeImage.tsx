import Image, { ImageProps } from 'next/image'

interface FixedSizeImageProps extends ImageProps {
  isCircle?: boolean
  size?: 'xs' | 'sm' | 'md' | 'lg'
  divClassName?: string
}

const sizeConfig = {
  xs: { imageSize: 24, divClass: 'w-6 h-6', rounded: 'rounded' },
  sm: { imageSize: 56, divClass: 'w-14 h-14', rounded: 'rounded-sm' },
  md: { imageSize: 128, divClass: 'w-32 h-32', rounded: 'rounded-md' },
  lg: { imageSize: 160, divClass: 'w-40 h-40', rounded: 'rounded-lg' },
} as const

const FixedSizeImage = ({
  isCircle = true,
  size = 'md',
  divClassName,
  className,
  ...rest
}: FixedSizeImageProps) => {
  const { imageSize, divClass, rounded } = sizeConfig[size]
  const roundedClass = isCircle ? 'rounded-full' : rounded

  return (
    <div className={`${divClass} overflow-hidden ${divClassName || ''}`}>
      <Image
        width={imageSize}
        height={imageSize}
        className={`h-full w-full object-cover object-top ${roundedClass} ${className || ''}`}
        {...rest}
      />
    </div>
  )
}

export default FixedSizeImage

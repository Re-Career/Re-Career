import Image, { ImageProps } from 'next/image'

interface FixedSizeImageProps extends ImageProps {
  isCircle?: boolean
  size?: 'xs' | 'sm' | 'md' | 'lg'
  divClassName?: string
}

const FixedSizeImage = ({
  isCircle = true,
  size = 'md',
  divClassName,
  className,
  ...rest
}: FixedSizeImageProps) => {
  const imageSizes = (): { divSize: number; imageSize: number } => {
    switch (size) {
      case 'xs':
        return {
          divSize: 6,
          imageSize: 24,
        }
      case 'sm':
        return {
          divSize: 14,
          imageSize: 56,
        }
      case 'md':
        return {
          divSize: 32,
          imageSize: 128,
        }
      case 'lg':
        return {
          divSize: 40,
          imageSize: 160,
        }
    }
  }

  const { divSize, imageSize } = imageSizes()

  return (
    <div
      className={`w-${divSize} h-${divSize} ${divClassName ? divClassName : ''}`}
    >
      <Image
        width={imageSize}
        height={imageSize}
        className={`h-full w-full object-cover object-top ${isCircle ? 'rounded-full' : `rounded-${size}`} ${className ? className : ''}`}
        {...rest}
      />
    </div>
  )
}

export default FixedSizeImage

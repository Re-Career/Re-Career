'use client'

import { WebViewMessageTypes } from '@/lib/constants/global'
import { Mentor } from '@/types/home'
import { isWebView, sendMessageToNative } from '@/utils/webview'
import Image from 'next/image'
import { useRouter } from 'next/navigation'

const MentorButton = ({ mentor }: { mentor: Mentor }) => {
  const router = useRouter()

  const handleMentorProfile = (id: number) => {
    if (isWebView()) {
      sendMessageToNative({
        type: WebViewMessageTypes.MENTOR_PROFILE,
        data: { mentorId: id },
      })
    } else {
      router.push(`mentor/${id}/profile`)
    }
  }

  return (
    <button
      key={mentor.id}
      className="flex flex-col items-center gap-3"
      onClick={() => handleMentorProfile(mentor.id)}
    >
      <div className="h-32 w-32 flex-shrink-0 overflow-hidden rounded-full">
        <Image
          src={mentor.profileImage}
          alt={mentor.name}
          width={128}
          height={128}
          className="h-full w-full object-cover"
        />
      </div>

      <div className="text-center">
        <h3 className="font-medium text-neutral-900">{mentor.name}</h3>
        <p className="text-sm text-gray-600">{mentor.job}</p>
      </div>
    </button>
  )
}

export default MentorButton

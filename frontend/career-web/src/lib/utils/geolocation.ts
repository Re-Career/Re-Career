export interface GeolocationPosition {
  latitude: number
  longitude: number
}

export interface GeolocationError {
  code: number
  message: string
}

export interface KoreanAddress {
  province: string // 도/특별시/광역시
  city: string // 시/군/구
}

export const getCurrentPosition = (): Promise<GeolocationPosition> => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject({
        code: 0,
        message: 'Geolocation is not supported by this browser.',
      } as GeolocationError)

      return
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        })
      },
      (error) => {
        reject({
          code: error.code,
          message: error.message,
        } as GeolocationError)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 60000,
      }
    )
  })
}

export const watchPosition = (
  callback: (position: GeolocationPosition) => void,
  errorCallback?: (error: GeolocationError) => void
): number | null => {
  if (!navigator.geolocation) {
    if (errorCallback) {
      errorCallback({
        code: 0,
        message: 'Geolocation is not supported by this browser.',
      })
    }

    return null
  }

  return navigator.geolocation.watchPosition(
    (position) => {
      callback({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      })
    },
    (error) => {
      if (errorCallback) {
        errorCallback({
          code: error.code,
          message: error.message,
        })
      }
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000,
    }
  )
}

export const clearWatch = (watchId: number): void => {
  if (navigator.geolocation) {
    navigator.geolocation.clearWatch(watchId)
  }
}

export const getKoreanAddress = async (
  latitude: number,
  longitude: number,
  kakaoApiKey: string
): Promise<KoreanAddress> => {
  const url = `https://dapi.kakao.com/v2/local/geo/coord2address.json?x=${longitude}&y=${latitude}`

  const response = await fetch(url, {
    headers: {
      Authorization: `KakaoAK ${kakaoApiKey}`,
    },
  })

  const data = await response.json()

  if (!response.ok) {
    throw new Error('주소 변환에 실패했습니다.')
  }

  if (!data.documents || data.documents.length === 0) {
    throw new Error('해당 좌표의 주소를 찾을 수 없습니다.')
  }

  const address = data.documents[0].address
  const roadAddress = data.documents[0].road_address

  // 도로명 주소가 있으면 우선 사용, 없으면 지번 주소 사용
  const targetAddress = roadAddress || address

  return {
    province: targetAddress.region_1depth_name, // 서울특별시, 경기도 등
    city: targetAddress.region_2depth_name, // 강남구, 수원시 등
  }
}

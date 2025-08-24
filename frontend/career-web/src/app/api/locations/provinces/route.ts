import { NextResponse } from 'next/server'
import { getProvinces } from '@/services/server/locations'

export async function GET() {
  const provinces = await getProvinces()

  return NextResponse.json(provinces, { status: 200 })
}

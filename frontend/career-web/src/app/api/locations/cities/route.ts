import { NextResponse } from 'next/server'
import { getCities } from '@/services/server/locations'

export async function GET() {
  const cities = await getCities()

  return NextResponse.json(cities, { status: 200 })
}

import { NextRequest, NextResponse } from 'next/server'
import { promises as fs } from 'fs'
import path from 'path'
import { formatImageId, checkFileNameExists } from '@/lib/image-utils'

const settings = require('@/lib/settings')

export const runtime = 'nodejs'

const MAX_IMAGE_ID = 32000

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const startId = parseInt(body.startId)
    const count = parseInt(body.count) || 1

    if (!startId || startId < 1 || startId > MAX_IMAGE_ID) {
      return NextResponse.json(
        { error: `Invalid start ID. Must be between 1 and ${MAX_IMAGE_ID}` },
        { status: 400 }
      )
    }

    if (count < 1) {
      return NextResponse.json(
        { error: 'Count must be at least 1' },
        { status: 400 }
      )
    }

    const endId = startId + count - 1
    if (endId > MAX_IMAGE_ID) {
      return NextResponse.json(
        { error: `End ID ${endId} exceeds maximum ${MAX_IMAGE_ID}` },
        { status: 400 }
      )
    }

    const resourcesDir = settings.getImagePath()
    const conflicts: number[] = []

    for (let id = startId; id <= endId; id++) {
      const fileName = formatImageId(id)
      const exists = await checkFileNameExists(fileName, resourcesDir, settings)
      if (exists) {
        conflicts.push(id)
      }
    }

    return NextResponse.json({
      success: true,
      startId,
      endId,
      count,
      conflicts,
      available: conflicts.length === 0,
      message: conflicts.length === 0
        ? `IDs ${startId} to ${endId} are available`
        : `Found ${conflicts.length} conflict(s) at IDs: ${conflicts.join(', ')}`,
    })
  } catch (error) {
    console.error('Check IDs error:', error)
    return NextResponse.json(
      { error: 'Failed to check IDs', details: error instanceof Error ? error.message : 'Unknown error' },
      { status: 500 }
    )
  }
}


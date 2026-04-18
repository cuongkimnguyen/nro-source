import { NextRequest, NextResponse } from 'next/server'
import { promises as fs } from 'fs'
import path from 'path'
import {
  processImageToAllSizes,
  formatImageId,
  checkFileNameExists,
} from '@/lib/image-utils'

const settings = require('@/lib/settings')

export const runtime = 'nodejs'

const MAX_IMAGE_ID = 32000

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get('file') as File
    const fileIndex = parseInt(formData.get('fileIndex') as string) || 0

    if (!file) {
      return NextResponse.json(
        { error: 'No file uploaded' },
        { status: 400 }
      )
    }

    const buffer = Buffer.from(await file.arrayBuffer())

    const resourcesDir = settings.getImagePath()
    await fs.mkdir(resourcesDir, { recursive: true })

    const startId = parseInt(formData.get('startId') as string)
    if (!startId || startId < 1 || startId > MAX_IMAGE_ID) {
      return NextResponse.json(
        { error: `Invalid start ID. Must be between 1 and ${MAX_IMAGE_ID}` },
        { status: 400 }
      )
    }

    const nextId = startId + fileIndex
    if (nextId > MAX_IMAGE_ID) {
      return NextResponse.json(
        { error: `Image ID ${nextId} exceeds maximum ${MAX_IMAGE_ID}` },
        { status: 400 }
      )
    }

    const fileName = formatImageId(nextId)
    const exists = await checkFileNameExists(fileName, resourcesDir, settings)
    
    if (exists) {
      return NextResponse.json(
        { error: `File ${fileName} already exists` },
        { status: 400 }
      )
    }

    const results = await processImageToAllSizes(buffer, fileName, resourcesDir, settings)

    return NextResponse.json({
      success: true,
      fileName,
      imageId: nextId,
      paths: results,
      message: 'Image processed and saved successfully',
    })
  } catch (error) {
    console.error('Upload error:', error)
    return NextResponse.json(
      { error: 'Failed to process image', details: error instanceof Error ? error.message : 'Unknown error' },
      { status: 500 }
    )
  }
}


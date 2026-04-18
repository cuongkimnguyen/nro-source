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

function detectIdFromFileName(fileName: string): number | null {
  const match = fileName.match(/(\d+)/)
  if (match) {
    const id = parseInt(match[1])
    if (id > 0 && id <= MAX_IMAGE_ID) {
      return id
    }
  }
  return null
}

function detectFrameFromFileName(fileName: string): number | null {
  const lowerName = fileName.toLowerCase()
  if (lowerName.includes('frame') || lowerName.includes('_f')) {
    const match = fileName.match(/[fF]rame[_-]?(\d+)|_f(\d+)/i)
    if (match) {
      return parseInt(match[1] || match[2]) || 6
    }
  }
  return null
}

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get('file') as File
    const type = formData.get('type') as string
    const imageId = formData.get('imageId') ? parseInt(formData.get('imageId') as string) : null
    const frame = formData.get('frame') ? parseInt(formData.get('frame') as string) : null

    if (!file) {
      return NextResponse.json(
        { error: 'Không có file được upload' },
        { status: 400 }
      )
    }

    if (!type || (type !== 'icon' && type !== 'effect')) {
      return NextResponse.json(
        { error: 'Type phải là "icon" hoặc "effect"' },
        { status: 400 }
      )
    }

    const buffer = Buffer.from(await file.arrayBuffer())
    const resourcesDir = settings.getImagePath()
    await fs.mkdir(resourcesDir, { recursive: true })

    let finalImageId = imageId
    if (!finalImageId) {
      finalImageId = detectIdFromFileName(file.name)
      if (!finalImageId) {
        return NextResponse.json(
          { error: 'Không thể detect ID từ tên file. Vui lòng nhập ID thủ công.' },
          { status: 400 }
        )
      }
    }

    if (finalImageId < 1 || finalImageId > MAX_IMAGE_ID) {
      return NextResponse.json(
        { error: `ID ảnh không hợp lệ. Phải từ 1 đến ${MAX_IMAGE_ID}` },
        { status: 400 }
      )
    }

    const fileName = formatImageId(finalImageId)
    const exists = await checkFileNameExists(fileName, resourcesDir, settings)
    
    if (exists) {
      return NextResponse.json(
        { error: `File ${fileName} đã tồn tại` },
        { status: 400 }
      )
    }

    const results = await processImageToAllSizes(buffer, fileName, resourcesDir, settings)

    // Icon không có frame, chỉ effect có frame
    let finalFrame: number | null = null
    if (type === 'effect') {
      finalFrame = frame !== null ? frame : detectFrameFromFileName(file.name) || 6
    }

    return NextResponse.json({
      success: true,
      fileName,
      imageId: finalImageId,
      frame: finalFrame,
      type,
      paths: results,
      message: 'Upload và xử lý ảnh thành công',
    })
  } catch (error) {
    console.error('Upload danh hieu error:', error)
    return NextResponse.json(
      { error: 'Upload thất bại', details: error instanceof Error ? error.message : 'Lỗi không xác định' },
      { status: 500 }
    )
  }
}

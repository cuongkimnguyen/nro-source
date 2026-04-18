import { NextRequest, NextResponse } from 'next/server'
import { promises as fs } from 'fs'
import path from 'path'
import { formatImageId } from '@/lib/image-utils'

const settings = require('@/lib/settings')

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const imageId = parseInt(body.imageId)

    if (!imageId || isNaN(imageId) || imageId < 1) {
      return NextResponse.json(
        { error: 'ID ảnh không hợp lệ' },
        { status: 400 }
      )
    }

    const fileName = formatImageId(imageId)
    const resourcesDir = settings.getImagePath()
    const activeSizes = settings.getActiveSizes()

    let foundImage: { path: string; size: string } | null = null

    // Thử nhiều cách tìm file
    const searchPaths: Array<{ path: string; size: string }> = []
    
    // 1. Tìm trong các thư mục size/small
    for (const size of activeSizes) {
      searchPaths.push({
        path: path.join(resourcesDir, size.folder, 'small', fileName),
        size: size.folder,
      })
    }
    
    // 2. Tìm trực tiếp trong thư mục size (không có small)
    for (const size of activeSizes) {
      searchPaths.push({
        path: path.join(resourcesDir, size.folder, fileName),
        size: size.folder,
      })
    }
    
    // 3. Tìm trong thư mục gốc resources/image
    searchPaths.push({
      path: path.join(resourcesDir, fileName),
      size: 'root',
    })
    
    // 4. Thử các format tên file khác
    const alternativeNames = [
      fileName,
      `Small${imageId}.png`,
      `small${imageId}.png`,
      `${imageId}.png`,
      `item_${imageId}.png`,
      `Item${imageId}.png`,
    ]
    
    for (const altName of alternativeNames) {
      for (const size of activeSizes) {
        searchPaths.push({
          path: path.join(resourcesDir, size.folder, 'small', altName),
          size: size.folder,
        })
        searchPaths.push({
          path: path.join(resourcesDir, size.folder, altName),
          size: size.folder,
        })
      }
      searchPaths.push({
        path: path.join(resourcesDir, altName),
        size: 'root',
      })
    }

    for (const searchPath of searchPaths) {
      try {
        await fs.access(searchPath.path)
        const fileBuffer = await fs.readFile(searchPath.path)
        const base64 = fileBuffer.toString('base64')
        const ext = path.extname(searchPath.path).toLowerCase()
        const mimeType = ext === '.png' ? 'image/png' : ext === '.jpg' || ext === '.jpeg' ? 'image/jpeg' : 'image/png'
        foundImage = {
          path: `data:${mimeType};base64,${base64}`,
          size: searchPath.size,
        }
        break
      } catch (err) {
        continue
      }
    }
    

    if (!foundImage) {
      return NextResponse.json({
        success: false,
        exists: false,
        message: `Không tìm thấy ảnh với ID ${imageId}`,
      })
    }

    return NextResponse.json({
      success: true,
      exists: true,
      imageId,
      fileName,
      imagePath: foundImage.path,
      size: foundImage.size,
      message: `Tìm thấy ảnh với ID ${imageId}`,
    })
  } catch (error) {
    console.error('Check image error:', error)
    return NextResponse.json(
      {
        error: 'Kiểm tra ảnh thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


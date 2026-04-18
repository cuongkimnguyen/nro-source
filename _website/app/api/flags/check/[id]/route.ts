import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const getFlagSql = 'SELECT * FROM nr_item WHERE id = ? AND type = 11'
    const result = await query(getFlagSql, [id])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { exists: false, message: 'Flag không tồn tại' },
        { status: 200 }
      )
    }

    const flag = result[0]

    let iconData = null
    if (flag.icon && flag.icon > 0) {
      try {
        const { promises: fs } = await import('fs')
        const path = await import('path')
        const { formatImageId } = await import('@/lib/image-utils')
        const settings = require('@/lib/settings')

        const fileName = formatImageId(flag.icon)
        const resourcesDir = settings.getImagePath()
        const activeSizes = settings.getActiveSizes()

        for (const size of activeSizes) {
          const filePath = path.join(resourcesDir, size.folder, 'small', fileName)
          try {
            await fs.access(filePath)
            const fileBuffer = await fs.readFile(filePath)
            const base64 = fileBuffer.toString('base64')
            const ext = path.extname(filePath).toLowerCase()
            const mimeType = ext === '.png' ? 'image/png' : ext === '.jpg' || ext === '.jpeg' ? 'image/jpeg' : 'image/png'
            iconData = {
              exists: true,
              imagePath: `data:${mimeType};base64,${base64}`,
              size: size.folder,
            }
            break
          } catch {
            continue
          }
        }
      } catch (error) {
        console.error('Check icon error:', error)
      }
    }

    return NextResponse.json({
      exists: true,
      flag: {
        ...flag,
        iconData,
      },
    })
  } catch (error) {
    console.error('Check flag error:', error)
    return NextResponse.json(
      {
        error: 'Kiểm tra flag thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


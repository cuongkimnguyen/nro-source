import { NextRequest, NextResponse } from 'next/server'
import { promises as fs } from 'fs'
import path from 'path'
import { resizeImage } from '@/lib/image-utils'

const settings = require('@/lib/settings')

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const resourcesDir = settings.getImagePath()
    const x2Path = path.join(resourcesDir, '2', 'small')
    const x3Path = path.join(resourcesDir, '3', 'small')
    const x4Path = path.join(resourcesDir, '4', 'small')

    // Đảm bảo các thư mục tồn tại
    await fs.mkdir(x2Path, { recursive: true })
    await fs.mkdir(x3Path, { recursive: true })
    await fs.mkdir(x4Path, { recursive: true })

    // Bước 1: Xóa toàn bộ ảnh trong x3 và x4
    console.log('Đang xóa ảnh trong x3 và x4...')
    
    const x3Files = await fs.readdir(x3Path).catch(() => [])
    const x4Files = await fs.readdir(x4Path).catch(() => [])

    for (const file of x3Files) {
      if (file.endsWith('.png')) {
        await fs.unlink(path.join(x3Path, file)).catch(() => {})
      }
    }

    for (const file of x4Files) {
      if (file.endsWith('.png')) {
        await fs.unlink(path.join(x4Path, file)).catch(() => {})
      }
    }

    console.log(`Đã xóa ${x3Files.length} ảnh trong x3 và ${x4Files.length} ảnh trong x4`)

    // Bước 2: Đọc tất cả ảnh x2
    const x2Files = (await fs.readdir(x2Path).catch(() => []))
      .filter(file => file.endsWith('.png'))
      .sort()

    if (x2Files.length === 0) {
      return NextResponse.json({
        success: false,
        message: 'Không tìm thấy ảnh nào trong thư mục x2',
      }, { status: 400 })
    }

    console.log(`Tìm thấy ${x2Files.length} ảnh trong x2, đang resize...`)

    // Bước 3: Resize từng ảnh x2 lên x3 (75%) và x4 (100%)
    let successCount = 0
    let errorCount = 0
    const errors: string[] = []

    for (const fileName of x2Files) {
      try {
        const x2FilePath = path.join(x2Path, fileName)
        const x3FilePath = path.join(x3Path, fileName)
        const x4FilePath = path.join(x4Path, fileName)

        // Đọc ảnh x2
        const x2Buffer = await fs.readFile(x2FilePath)

        // Resize lên 75% cho x3 (x2 là 50%, nên 75% / 50% = 1.5)
        // Nhưng thực ra x2 đã là 50% của ảnh gốc, nên để có 75% của ảnh gốc, ta cần resize x2 lên 1.5 lần
        // Tương tự, để có 100% của ảnh gốc, ta cần resize x2 lên 2 lần
        await resizeImage(x2Buffer, x3FilePath, 1.5, true) // 50% * 1.5 = 75%, allowEnlargement = true
        await resizeImage(x2Buffer, x4FilePath, 2.0, true) // 50% * 2.0 = 100%, allowEnlargement = true

        successCount++
      } catch (error) {
        errorCount++
        errors.push(`${fileName}: ${error instanceof Error ? error.message : 'Unknown error'}`)
        console.error(`Lỗi khi xử lý ${fileName}:`, error)
      }
    }

    return NextResponse.json({
      success: true,
      message: `Hoàn tất! Đã xử lý ${successCount} ảnh thành công${errorCount > 0 ? `, ${errorCount} ảnh gặp lỗi` : ''}`,
      stats: {
        deletedX3: x3Files.length,
        deletedX4: x4Files.length,
        processed: successCount,
        errors: errorCount,
        errorDetails: errors.slice(0, 10), // Chỉ trả về 10 lỗi đầu tiên
      },
    })
  } catch (error) {
    console.error('Resize error:', error)
    return NextResponse.json(
      { 
        success: false,
        error: 'Failed to resize images', 
        details: error instanceof Error ? error.message : 'Unknown error' 
      },
      { status: 500 }
    )
  }
}

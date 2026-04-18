import { NextRequest, NextResponse } from 'next/server'
import { promises as fs } from 'fs'
import path from 'path'

export const runtime = 'nodejs'

const DANH_HIEU_FILE_PATH = path.resolve(process.cwd(), '..', 'src', 'main', 'java', 'com', 'ngocrong', 'user', 'DanhHieu.java')

export async function POST(request: NextRequest) {
  try {
    const { itemId, effectImageId, frame } = await request.json()

    if (!itemId || !effectImageId || !frame) {
      return NextResponse.json(
        { error: 'Thiếu thông tin: itemId, effectImageId, frame' },
        { status: 400 }
      )
    }

    const codeLine = `        danhHieuMap.put(${itemId}, new int[]{${effectImageId}, ${frame}});`

    let fileContent = await fs.readFile(DANH_HIEU_FILE_PATH, 'utf-8')

    const lines = fileContent.split('\n')
    const loggerLineIndex = lines.findIndex((line) => line.includes('logger.info("Đã load'))
    
    if (loggerLineIndex === -1) {
      return NextResponse.json(
        { error: 'Không tìm thấy dòng logger.info trong file' },
        { status: 500 }
      )
    }

    const existingLine = lines.find((line) => line.trim() === codeLine.trim())
    if (existingLine) {
      return NextResponse.json(
        { error: 'Code đã tồn tại trong file', alreadyExists: true },
        { status: 400 }
      )
    }

    lines.splice(loggerLineIndex, 0, codeLine)

    const newContent = lines.join('\n')
    await fs.writeFile(DANH_HIEU_FILE_PATH, newContent, 'utf-8')

    return NextResponse.json({
      success: true,
      message: `Đã thêm code vào DanhHieu.java`,
      code: codeLine,
    })
  } catch (error) {
    console.error('Add danh hieu code error:', error)
    return NextResponse.json(
      {
        error: 'Thêm code vào DanhHieu.java thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

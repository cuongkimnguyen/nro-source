import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const { name } = body

    if (!name || name.trim() === '') {
      return NextResponse.json(
        { error: 'Vui lòng nhập tên cải trang' },
        { status: 400 }
      )
    }

    const checkSql = 'SELECT id, name, type FROM nr_item WHERE name = ? AND type = 5'
    const result = await query(checkSql, [name.trim()])

    if (result && result.length > 0) {
      return NextResponse.json({
        success: true,
        exists: true,
        id: result[0].id,
        name: result[0].name,
        message: `Cải trang "${name}" đã tồn tại`,
      })
    }

    return NextResponse.json({
      success: true,
      exists: false,
      message: `Cải trang "${name}" chưa tồn tại`,
    })
  } catch (error) {
    console.error('Check costume error:', error)
    return NextResponse.json(
      {
        error: 'Kiểm tra cải trang thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


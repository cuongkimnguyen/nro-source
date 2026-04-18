import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const itemId = parseInt(searchParams.get('itemId') || '0')

    if (!itemId || isNaN(itemId) || itemId < 0) {
      return NextResponse.json(
        { error: 'Item ID không hợp lệ' },
        { status: 400 }
      )
    }

    const sql = `SELECT icon FROM nr_item WHERE id = ? LIMIT 1`
    const results = await query(sql, [itemId])

    if (!results || results.length === 0) {
      return NextResponse.json({
        success: false,
        iconId: null,
        message: `Không tìm thấy item với ID ${itemId}`,
      })
    }

    const iconId = results[0].icon

    return NextResponse.json({
      success: true,
      itemId,
      iconId: iconId || null,
      message: iconId ? `Tìm thấy icon ID: ${iconId}` : `Item ${itemId} không có icon`,
    })
  } catch (error) {
    console.error('Get item icon error:', error)
    return NextResponse.json(
      {
        error: 'Lấy icon ID thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const sql = `
      SELECT 
        id, name, type
      FROM nr_item_option_template
      ORDER BY id ASC
    `
    const results = await query(sql)

    return NextResponse.json({
      success: true,
      options: results,
    })
  } catch (error) {
    console.error('Get item options error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách options thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const table = searchParams.get('table')

    if (!table) {
      return NextResponse.json(
        { error: 'Thiếu tham số table' },
        { status: 400 }
      )
    }

    const getItemsSql = `
      SELECT 
        s.*,
        i.icon as item_icon,
        i.name as item_name
      FROM \`${table}\` s
      LEFT JOIN nr_item i ON s.item_id = i.id
      ORDER BY s.tab ASC, s.id DESC
    `
    const items = await query(getItemsSql, [])

    return NextResponse.json({
      success: true,
      items: items || [],
    })
  } catch (error) {
    console.error('Get shop items error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách shop items thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


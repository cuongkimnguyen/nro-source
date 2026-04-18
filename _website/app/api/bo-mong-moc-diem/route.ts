import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const page = parseInt(searchParams.get('page') || '1')
    const limit = parseInt(searchParams.get('limit') || '20')

    const offset = (page - 1) * limit

    const countSql = `SELECT COUNT(*) as total FROM nr_bo_mong_moc_diem`
    const countResult = await query(countSql)
    const total = countResult[0]?.total || 0

    const sql = `
      SELECT 
        id, diem_can_thiet, item_id_1, item_id_2, item_id_3, item_id_4,
        active, sort_order, created_at, updated_at
      FROM nr_bo_mong_moc_diem
      ORDER BY sort_order ASC, diem_can_thiet ASC
      LIMIT ? OFFSET ?
    `
    const results = await query(sql, [limit, offset])

    return NextResponse.json({
      success: true,
      data: results,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    })
  } catch (error) {
    console.error('Get bo mong moc diem error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách mốc điểm thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const {
      diem_can_thiet,
      item_id_1,
      item_id_2,
      item_id_3,
      item_id_4,
      active = 1,
      sort_order = 0,
    } = body

    if (!diem_can_thiet || diem_can_thiet < 0) {
      return NextResponse.json(
        { error: 'Điểm cần thiết không hợp lệ' },
        { status: 400 }
      )
    }

    const checkSql = 'SELECT id FROM nr_bo_mong_moc_diem WHERE diem_can_thiet = ?'
    const existing = await query(checkSql, [diem_can_thiet])
    
    if (existing && existing.length > 0) {
      return NextResponse.json(
        { error: `Mốc điểm ${diem_can_thiet} đã tồn tại` },
        { status: 400 }
      )
    }

    const insertSql = `
      INSERT INTO nr_bo_mong_moc_diem (
        diem_can_thiet, item_id_1, item_id_2, item_id_3, item_id_4,
        active, sort_order
      ) VALUES (?, ?, ?, ?, ?, ?, ?)
    `

    const result = await query(insertSql, [
      diem_can_thiet,
      item_id_1 || null,
      item_id_2 || null,
      item_id_3 || null,
      item_id_4 || null,
      active,
      sort_order,
    ])

    const insertId = (result as any).insertId

    return NextResponse.json({
      success: true,
      message: `Đã tạo mốc điểm ${diem_can_thiet} thành công`,
      data: {
        id: insertId,
        diem_can_thiet,
      },
    })
  } catch (error: any) {
    console.error('Create bo mong moc diem error:', error)
    if (error.code === 'ER_DUP_ENTRY') {
      return NextResponse.json(
        { error: 'Mốc điểm đã tồn tại' },
        { status: 400 }
      )
    }
    return NextResponse.json(
      {
        error: 'Tạo mốc điểm thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

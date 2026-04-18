import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

const ITEMS_PER_PAGE = 50

const ITEM_TYPES: Record<number, string> = {
  0: 'Áo',
  1: 'Quần',
  2: 'Găng tay',
  3: 'Giày',
  4: 'Rada',
  5: 'Tóc',
  6: 'Đầu thân',
  7: 'Sách',
  8: 'Nhiệm vụ',
  9: 'Vàng',
  10: 'Ngọc',
  11: 'Balo',
  12: 'Ngọc rồng',
  13: 'Bùa',
  17: 'Danh hiệu',
  18: 'Pet theo sau',
  19: 'Pet bay',
  21: 'Pet bay bạc 1',
  23: 'Thú cưng 1',
  24: 'Thú cưng 2',
  26: 'Ngọc bội',
  27: 'Vật phẩm phụ trợ',
  34: 'Diamond lock',
  35: 'Hào quang',
  36: 'Cải trang',
  38: 'Pet bay bạc 2',
  39: 'Vật phẩm phụ trợ',
}

const GENDERS: Record<number, string> = {
  0: 'Trái Đất',
  1: 'Namec',
  2: 'Xayda',
  3: 'Tất cả',
}

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const page = parseInt(searchParams.get('page') || '1')
    const type = searchParams.get('type')
    const gender = searchParams.get('gender')
    const search = searchParams.get('search') || ''
    const limit = parseInt(searchParams.get('limit') || ITEMS_PER_PAGE.toString())

    const offset = (page - 1) * limit

    let whereConditions: string[] = []
    let queryParams: any[] = []

    if (type !== null && type !== '' && type !== 'all') {
      whereConditions.push('`type` = ?')
      queryParams.push(parseInt(type))
    }

    if (gender !== null && gender !== '' && gender !== 'all') {
      whereConditions.push('`gender` = ?')
      queryParams.push(parseInt(gender))
    }

    if (search.trim() !== '') {
      const searchTerm = `%${search.trim()}%`
      whereConditions.push('(`name` LIKE ? OR `id` = ?)')
      queryParams.push(searchTerm)
      const searchId = parseInt(search.trim())
      queryParams.push(isNaN(searchId) ? -1 : searchId)
    }

    const whereClause = whereConditions.length > 0
      ? `WHERE ${whereConditions.join(' AND ')}`
      : ''

    const countSql = `SELECT COUNT(*) as total FROM nr_item ${whereClause}`
    const countResult = await query(countSql, queryParams)
    const total = countResult[0]?.total || 0

    const itemsSql = `
      SELECT 
        id, name, type, gender, description, level, \`require\`,
        resale_price, icon, part, is_up_to_up,
        head, body, leg, options, mount_id, \`lock\`
      FROM nr_item
      ${whereClause}
      ORDER BY id DESC
      LIMIT ? OFFSET ?
    `
    queryParams.push(limit, offset)
    const items = await query(itemsSql, queryParams)

    const itemsWithLabels = items.map((item: any) => ({
      ...item,
      typeLabel: ITEM_TYPES[item.type] || `Type ${item.type}`,
      genderLabel: GENDERS[item.gender] || `Gender ${item.gender}`,
    }))

    return NextResponse.json({
      success: true,
      items: itemsWithLabels,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    })
  } catch (error) {
    console.error('Get items error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách items thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


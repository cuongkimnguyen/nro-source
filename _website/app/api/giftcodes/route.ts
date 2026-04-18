import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

const CODES_PER_PAGE = 20

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const page = parseInt(searchParams.get('page') || '1')
    const search = searchParams.get('search') || ''
    const limit = parseInt(searchParams.get('limit') || CODES_PER_PAGE.toString())

    const offset = (page - 1) * limit

    let whereConditions: string[] = []
    let queryParams: any[] = []

    if (search.trim() !== '') {
      const searchTerm = `%${search.trim()}%`
      whereConditions.push('(`code` LIKE ? OR `id` = ?)')
      queryParams.push(searchTerm)
      const searchId = parseInt(search.trim())
      queryParams.push(isNaN(searchId) ? -1 : searchId)
    }

    const whereClause = whereConditions.length > 0
      ? `WHERE ${whereConditions.join(' AND ')}`
      : ''

    const countSql = `SELECT COUNT(*) as total FROM nr_gift_code ${whereClause}`
    const countResult = await query(countSql, queryParams)
    const total = countResult[0]?.total || 0

    const codesSql = `
      SELECT 
        id, server_id, code, is_activated, gold, diamond, diamond_lock,
        items, expires_at, created_at
      FROM nr_gift_code
      ${whereClause}
      ORDER BY id DESC
      LIMIT ? OFFSET ?
    `
    queryParams.push(limit, offset)
    const codes = await query(codesSql, queryParams)

    const now = new Date()
    const codesWithStatus = codes.map((code: any) => {
      const expiresAt = new Date(code.expires_at)
      const createdAt = new Date(code.created_at)
      const isExpired = now > expiresAt
      const isNotStarted = now < createdAt
      const isActive = !isExpired && !isNotStarted

      return {
        ...code,
        status: isExpired ? 'expired' : isNotStarted ? 'not_started' : 'active',
        isExpired,
        isNotStarted,
        isActive,
      }
    })

    return NextResponse.json({
      success: true,
      codes: codesWithStatus,
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
      },
    })
  } catch (error) {
    console.error('Get giftcodes error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách giftcode thất bại',
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
      code,
      server_id = 1,
      gold = 0,
      diamond = 0,
      diamond_lock = 0,
      items = '[]',
      expires_at,
      created_at,
    } = body

    if (!code || code.trim() === '') {
      return NextResponse.json(
        { error: 'Code không được để trống' },
        { status: 400 }
      )
    }

    if (code.length < 5 || code.length > 30) {
      return NextResponse.json(
        { error: 'Code phải có độ dài từ 5 đến 30 ký tự' },
        { status: 400 }
      )
    }

    let itemsJson = '[]'
    if (items && items.trim() !== '') {
      try {
        const parsed = typeof items === 'string' ? JSON.parse(items) : items
        if (!Array.isArray(parsed)) {
          return NextResponse.json(
            { error: 'Items phải là một mảng JSON' },
            { status: 400 }
          )
        }
        itemsJson = JSON.stringify(parsed)
      } catch (err) {
        return NextResponse.json(
          { error: 'Items JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
          { status: 400 }
        )
      }
    }

    const codeUpper = code.toUpperCase().trim()

    const checkCodeSql = 'SELECT id FROM nr_gift_code WHERE code = ?'
    const existing = await query(checkCodeSql, [codeUpper])
    
    if (existing && existing.length > 0) {
      return NextResponse.json(
        { error: `Code "${codeUpper}" đã tồn tại` },
        { status: 400 }
      )
    }

    const expiresAt = expires_at ? new Date(expires_at) : new Date(Date.now() + 365 * 24 * 60 * 60 * 1000)
    const createdAt = created_at ? new Date(created_at) : new Date()

    const insertSql = `
      INSERT INTO nr_gift_code (
        server_id, code, is_activated, gold, diamond, diamond_lock,
        items, expires_at, created_at
      ) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?)
    `

    const result = await query(insertSql, [
      server_id,
      codeUpper,
      gold,
      diamond,
      diamond_lock,
      itemsJson,
      expiresAt,
      createdAt,
    ])

    const insertId = (result as any).insertId

    return NextResponse.json({
      success: true,
      message: `Đã tạo giftcode "${codeUpper}" thành công`,
      data: {
        id: insertId,
        code: codeUpper,
      },
    })
  } catch (error: any) {
    console.error('Create giftcode error:', error)
    if (error.code === 'ER_DUP_ENTRY') {
      return NextResponse.json(
        { error: 'Code đã tồn tại' },
        { status: 400 }
      )
    }
    return NextResponse.json(
      {
        error: 'Tạo giftcode thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


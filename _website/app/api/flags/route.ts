import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const page = parseInt(searchParams.get('page') || '1')
    const limit = parseInt(searchParams.get('limit') || '50')
    const searchTerm = searchParams.get('search')

    const offset = (page - 1) * limit

    let whereClauses = ['type = 11']
    let params: any[] = []

    if (searchTerm) {
      whereClauses.push('(name LIKE ? OR id = ?)')
      params.push(`%${searchTerm}%`)
      params.push(parseInt(searchTerm) || 0)
    }

    const whereSql = `WHERE ${whereClauses.join(' AND ')}`

    const countSql = `SELECT COUNT(*) as total FROM nr_item ${whereSql}`
    const countResult = await query(countSql, params)
    const totalFlags = countResult[0]?.total || 0

    const flagsSql = `
      SELECT * FROM nr_item
      ${whereSql}
      ORDER BY id DESC
      LIMIT ? OFFSET ?
    `
    const flags = await query(flagsSql, [...params, limit, offset])

    return NextResponse.json({
      success: true,
      flags: flags || [],
      totalFlags,
      totalPages: Math.ceil(totalFlags / limit),
      currentPage: page,
    })
  } catch (error) {
    console.error('Get flags error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách flags thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function POST(request: NextRequest) {
  try {
    const {
      name,
      description,
      icon,
      gender,
      level,
      require,
      resale_price,
      options,
      is_up_to_up,
      bag_id,
      bag_name,
      bag_images,
    } = await request.json()

    if (!name || !icon) {
      return NextResponse.json(
        { error: 'Thiếu các trường bắt buộc: name, icon' },
        { status: 400 }
      )
    }

    const getMaxIdSql = 'SELECT MAX(id) as maxId FROM nr_item'
    const maxIdResult = await query(getMaxIdSql, [])
    const newId = (maxIdResult[0]?.maxId || 0) + 1

    let optionsJson = '[]'
    if (options) {
      try {
        const parsedOptions = JSON.parse(options)
        if (!Array.isArray(parsedOptions)) {
          return NextResponse.json(
            { error: 'Options phải là một mảng JSON hợp lệ' },
            { status: 400 }
          )
        }
        optionsJson = options
      } catch (jsonError) {
        return NextResponse.json(
          { error: 'Options JSON không hợp lệ' },
          { status: 400 }
        )
      }
    }

    if (!bag_id || parseInt(bag_id) < 0) {
      return NextResponse.json(
        { error: 'Bag ID phải >= 0' },
        { status: 400 }
      )
    }
    if (!bag_name || !bag_images) {
      return NextResponse.json(
        { error: 'Thiếu thông tin Bag: bag_name, bag_images' },
        { status: 400 }
      )
    }

    const bagIdValue = parseInt(bag_id)
    let bagImagesJson = '[]'
    try {
      const parsedImages = JSON.parse(bag_images)
      if (!Array.isArray(parsedImages)) {
        return NextResponse.json(
          { error: 'bag_images phải là một mảng JSON hợp lệ' },
          { status: 400 }
        )
      }
      bagImagesJson = bag_images
    } catch (jsonError) {
      return NextResponse.json(
        { error: 'bag_images JSON không hợp lệ' },
        { status: 400 }
      )
    }

    const checkClanImageSql = 'SELECT id FROM nr_clan_image WHERE id = ?'
    const existingClanImage = await query(checkClanImageSql, [bagIdValue])
    
    if (existingClanImage && existingClanImage.length > 0) {
      return NextResponse.json(
        { error: `Bag ID ${bagIdValue} đã tồn tại trong nr_clan_image` },
        { status: 400 }
      )
    }

    const insertClanImageSql = `
      INSERT INTO nr_clan_image (id, name, images, gold, gem, is_sale)
      VALUES (?, ?, ?, 0, 0, 0)
    `
    await query(insertClanImageSql, [
      bagIdValue,
      bag_name,
      bagImagesJson,
    ])

    const insertSql = `
      INSERT INTO nr_item (
        id, name, type, gender, description, level, \`require\`,
        resale_price, icon, part, is_up_to_up,
        head, body, leg, options, mount_id, \`lock\`
      ) VALUES (?, ?, 11, ?, ?, ?, ?, ?, ?, ?, ?, -1, -1, -1, ?, -1, 0)
    `

    await query(insertSql, [
      newId,
      name,
      gender || 3,
      description || '',
      level || 0,
      require || 0,
      resale_price || -1,
      icon,
      bagIdValue,
      is_up_to_up || 0,
      optionsJson,
    ])

    return NextResponse.json({
      success: true,
      message: 'Thêm flag thành công',
      id: newId,
    })
  } catch (error) {
    console.error('Add flag error:', error)
    return NextResponse.json(
      {
        error: 'Thêm flag thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}
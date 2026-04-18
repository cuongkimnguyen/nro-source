import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const page = parseInt(searchParams.get('page') || '1')
    const limit = parseInt(searchParams.get('limit') || '20')
    const search = searchParams.get('search') || ''
    const offset = (page - 1) * limit

    const params: any[] = []
    let whereSql = 'WHERE type = 18'

    if (search) {
      whereSql += ' AND (name LIKE ? OR description LIKE ?)'
      params.push(`%${search}%`, `%${search}%`)
    }

    const countSql = `SELECT COUNT(*) as total FROM nr_item ${whereSql}`
    const countResult = await query(countSql, params)
    const totalPets = countResult[0]?.total || 0

    const petsSql = `
      SELECT * FROM nr_item
      ${whereSql}
      ORDER BY id DESC
      LIMIT ? OFFSET ?
    `
    const pets = await query(petsSql, [...params, limit, offset])

    return NextResponse.json({
      success: true,
      pets,
      pagination: {
        page,
        limit,
        total: totalPets,
        totalPages: Math.ceil(totalPets / limit),
      },
    })
  } catch (error) {
    console.error('Get pets error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách pet thất bại',
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
      head = -1,
      body = -1,
      leg = -1,
      partHeadJson = '',
      partBodyJson = '',
      partLegJson = '',
      options,
      is_up_to_up,
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

    const hasItemValues = (head > 0) || (body > 0) || (leg > 0)
    const hasPartValues = (partHeadJson && partHeadJson.trim() !== '') ||
                          (partBodyJson && partBodyJson.trim() !== '') ||
                          (partLegJson && partLegJson.trim() !== '')

    if (hasItemValues && hasPartValues) {
      return NextResponse.json(
        { error: 'Không thể điền cả Part ID có sẵn và JSON cùng lúc. Chọn một trong hai cách.' },
        { status: 400 }
      )
    }

    if (!hasItemValues && !hasPartValues) {
      return NextResponse.json(
        { error: 'Vui lòng chọn một trong hai: Part ID có sẵn hoặc tạo mới từ JSON' },
        { status: 400 }
      )
    }

    let finalHead = head > 0 ? head : -1
    let finalBody = body > 0 ? body : -1
    let finalLeg = leg > 0 ? leg : -1

    const defaultPartData = JSON.stringify([{ id: 0, dx: 0, dy: 0 }])

    if (hasPartValues) {
      const maxPartIdSql = 'SELECT MAX(id) as maxPartId FROM nr_part'
      const maxPartIdResult = await query(maxPartIdSql, [])
      const basePartId = maxPartIdResult && maxPartIdResult.length > 0 && maxPartIdResult[0].maxPartId 
        ? parseInt(maxPartIdResult[0].maxPartId) + 1 
        : 1

      let partHeadData = defaultPartData
      if (partHeadJson && partHeadJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partHeadJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Head JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partHeadData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Head JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      const insertPartHeadSql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 0, ?, ?)'
      await query(insertPartHeadSql, [basePartId, partHeadData, ''])
      finalHead = basePartId

      let partBodyData = defaultPartData
      if (partBodyJson && partBodyJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partBodyJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Body JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partBodyData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Body JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      const insertPartBodySql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 1, ?, ?)'
      await query(insertPartBodySql, [basePartId + 1, partBodyData, ''])
      finalBody = basePartId + 1

      let partLegData = defaultPartData
      if (partLegJson && partLegJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partLegJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Leg JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partLegData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Leg JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      const insertPartLegSql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 2, ?, ?)'
      await query(insertPartLegSql, [basePartId + 2, partLegData, ''])
      finalLeg = basePartId + 2
    } else {
      const headValue = parseInt(head) || -1
      const bodyValue = parseInt(body) || -1
      const legValue = parseInt(leg) || -1

      const checkPartSql = 'SELECT id FROM nr_part WHERE id IN (?, ?, ?)'
      const partResult = await query(checkPartSql, [headValue, bodyValue, legValue])
      
      if (!partResult || partResult.length !== 3) {
        return NextResponse.json(
          { error: 'Một hoặc nhiều Part ID không tồn tại trong nr_part' },
          { status: 400 }
        )
      }

      finalHead = headValue
      finalBody = bodyValue
      finalLeg = legValue
    }

    const insertSql = `
      INSERT INTO nr_item (
        id, name, type, gender, description, level, \`require\`,
        resale_price, icon, part, is_up_to_up,
        head, body, leg, options, mount_id, \`lock\`
      ) VALUES (?, ?, 18, ?, ?, ?, ?, ?, ?, -1, ?, ?, ?, ?, ?, -1, 0)
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
      is_up_to_up || 0,
      finalHead,
      finalBody,
      finalLeg,
      optionsJson,
    ])

    return NextResponse.json({
      success: true,
      message: 'Thêm pet thành công',
      id: newId,
    })
  } catch (error) {
    console.error('Add pet error:', error)
    return NextResponse.json(
      {
        error: 'Thêm pet thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


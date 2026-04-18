import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const requestBody = await request.json()
    const {
      name,
      description,
      icon,
      gender = 3,
      level = 0,
      require = 0,
      resale_price = -1,
      options = '[]',
    } = requestBody

    if (!name || !description || !icon) {
      return NextResponse.json(
        { error: 'Vui lòng điền đầy đủ thông tin: tên, mô tả và icon' },
        { status: 400 }
      )
    }

    const maxIdSql = 'SELECT MAX(id) as maxId FROM nr_item'
    const maxIdResult = await query(maxIdSql, [])
    const maxId = maxIdResult && maxIdResult.length > 0 && maxIdResult[0].maxId 
      ? parseInt(maxIdResult[0].maxId) + 1 
      : 1
    
    const id = maxId

    let optionsJson = '[]'
    if (options && options.trim() !== '') {
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

    const insertSql = `
      INSERT INTO nr_item (
        id, name, type, gender, description, level, \`require\`,
        resale_price, icon, part, is_up_to_up,
        head, body, leg, options, mount_id, \`lock\`
      ) VALUES (?, ?, 17, ?, ?, ?, ?, ?, ?, -1, 0, -1, -1, -1, ?, -1, 0)
    `

    await query(insertSql, [
      id,
      name,
      gender,
      description,
      level,
      require,
      resale_price,
      icon,
      optionsJson,
    ])

    const message = `Đã thêm danh hiệu "${name}" (ID: ${id}) thành công`

    return NextResponse.json({
      success: true,
      message,
      id,
      data: {
        id,
        name,
        description,
        icon,
        gender,
        level,
        require,
        resale_price,
        options: optionsJson,
      },
    })
  } catch (error) {
    console.error('Add danh hieu error:', error)
    return NextResponse.json(
      {
        error: 'Thêm danh hiệu thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

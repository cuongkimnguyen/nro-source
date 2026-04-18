import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const getPetSql = 'SELECT * FROM nr_item WHERE id = ? AND type = 18'
    const result = await query(getPetSql, [id])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { error: 'Pet không tồn tại' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      pet: result[0],
    })
  } catch (error) {
    console.error('Get pet error:', error)
    return NextResponse.json(
      {
        error: 'Lấy thông tin pet thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function PUT(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id
    const {
      name,
      description,
      icon,
      gender,
      level,
      require,
      resale_price,
      head,
      body,
      leg,
      options,
      is_up_to_up,
    } = await request.json()

    if (!name || !icon) {
      return NextResponse.json(
        { error: 'Thiếu các trường bắt buộc: name, icon' },
        { status: 400 }
      )
    }

    if (!head || !body || !leg) {
      return NextResponse.json(
        { error: 'Thiếu các trường bắt buộc: head, body, leg (Part ID)' },
        { status: 400 }
      )
    }

    const checkPetSql = 'SELECT id FROM nr_item WHERE id = ? AND type = 18'
    const checkResult = await query(checkPetSql, [id])
    if (checkResult.length === 0) {
      return NextResponse.json(
        { error: 'Pet không tồn tại' },
        { status: 404 }
      )
    }

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

    const updateSql = `
      UPDATE nr_item
      SET 
        name = ?,
        description = ?,
        icon = ?,
        gender = ?,
        level = ?,
        \`require\` = ?,
        resale_price = ?,
        head = ?,
        body = ?,
        leg = ?,
        options = ?,
        is_up_to_up = ?
      WHERE id = ? AND type = 18
    `

    const [result]: any = await query(updateSql, [
      name,
      description || '',
      icon,
      gender || 3,
      level || 0,
      require || 0,
      resale_price || -1,
      headValue,
      bodyValue,
      legValue,
      optionsJson,
      is_up_to_up || 0,
      id,
    ])

    if (result.affectedRows === 0) {
      return NextResponse.json(
        { error: 'Không tìm thấy pet hoặc không có gì thay đổi' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      message: 'Cập nhật pet thành công',
    })
  } catch (error) {
    console.error('Update pet error:', error)
    return NextResponse.json(
      {
        error: 'Cập nhật pet thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function DELETE(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const checkPetSql = 'SELECT id FROM nr_item WHERE id = ? AND type = 18'
    const checkResult = await query(checkPetSql, [id])
    if (checkResult.length === 0) {
      return NextResponse.json(
        { error: 'Pet không tồn tại' },
        { status: 404 }
      )
    }

    const deleteSql = 'DELETE FROM nr_item WHERE id = ? AND type = 18'
    const [result]: any = await query(deleteSql, [id])

    if (result.affectedRows === 0) {
      return NextResponse.json(
        { error: 'Không thể xóa pet' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      message: 'Xóa pet thành công',
    })
  } catch (error) {
    console.error('Delete pet error:', error)
    return NextResponse.json(
      {
        error: 'Xóa pet thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


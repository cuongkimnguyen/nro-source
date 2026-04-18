import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const getFlagSql = 'SELECT * FROM nr_item WHERE id = ? AND type = 11'
    const result = await query(getFlagSql, [id])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { error: 'Flag không tồn tại' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      flag: result[0],
    })
  } catch (error) {
    console.error('Get flag error:', error)
    return NextResponse.json(
      {
        error: 'Lấy thông tin flag thất bại',
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
      part,
      options,
      is_up_to_up,
    } = await request.json()

    if (!name || !icon) {
      return NextResponse.json(
        { error: 'Thiếu các trường bắt buộc: name, icon' },
        { status: 400 }
      )
    }

    const checkFlagSql = 'SELECT id FROM nr_item WHERE id = ? AND type = 11'
    const checkResult = await query(checkFlagSql, [id])
    if (checkResult.length === 0) {
      return NextResponse.json(
        { error: 'Flag không tồn tại' },
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

    const partValue = part !== undefined && part !== null ? (part === -1 ? -1 : part) : -1

    if (partValue !== -1 && partValue >= 0) {
      const checkClanImageSql = 'SELECT id FROM nr_clan_image WHERE id = ?'
      const clanImageResult = await query(checkClanImageSql, [partValue])
      
      if (!clanImageResult || clanImageResult.length === 0) {
        return NextResponse.json(
          { 
            error: `Bag ID ${partValue} không tồn tại trong nr_clan_image. Vui lòng tạo record trong nr_clan_image trước.` 
          },
          { status: 400 }
        )
      }
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
        part = ?,
        options = ?,
        is_up_to_up = ?
      WHERE id = ? AND type = 11
    `

    const [result]: any = await query(updateSql, [
      name,
      description || '',
      icon,
      gender || 3,
      level || 0,
      require || 0,
      resale_price || -1,
      partValue,
      optionsJson,
      is_up_to_up || 0,
      id,
    ])

    if (result.affectedRows === 0) {
      return NextResponse.json(
        { error: 'Không tìm thấy flag hoặc không có gì thay đổi' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      message: 'Cập nhật flag thành công',
    })
  } catch (error) {
    console.error('Update flag error:', error)
    return NextResponse.json(
      {
        error: 'Cập nhật flag thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function DELETE(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const checkFlagSql = 'SELECT id FROM nr_item WHERE id = ? AND type = 11'
    const checkResult = await query(checkFlagSql, [id])
    if (checkResult.length === 0) {
      return NextResponse.json(
        { error: 'Flag không tồn tại' },
        { status: 404 }
      )
    }

    const deleteSql = 'DELETE FROM nr_item WHERE id = ? AND type = 11'
    const [result]: any = await query(deleteSql, [id])

    if (result.affectedRows === 0) {
      return NextResponse.json(
        { error: 'Không thể xóa flag' },
        { status: 404 }
      )
    }

    return NextResponse.json({
      success: true,
      message: 'Xóa flag thành công',
    })
  } catch (error) {
    console.error('Delete flag error:', error)
    return NextResponse.json(
      {
        error: 'Xóa flag thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


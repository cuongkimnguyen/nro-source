import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const id = parseInt(params.id)

    if (isNaN(id)) {
      return NextResponse.json(
        { error: 'ID không hợp lệ' },
        { status: 400 }
      )
    }

    const getCodeSql = `
      SELECT 
        id, server_id, code, is_activated, gold, diamond, diamond_lock,
        items, expires_at, created_at
      FROM nr_gift_code
      WHERE id = ?
    `
    const codes = await query(getCodeSql, [id])

    if (!codes || codes.length === 0) {
      return NextResponse.json(
        { error: 'Giftcode không tồn tại' },
        { status: 404 }
      )
    }

    const codeData = codes[0]
    let itemsParsed: any[] = []
    try {
      itemsParsed = JSON.parse(codeData.items || '[]')
    } catch {}

    return NextResponse.json({
      success: true,
      code: {
        ...codeData,
        items: itemsParsed,
      },
    })
  } catch (error) {
    console.error('Get giftcode error:', error)
    return NextResponse.json(
      {
        error: 'Lấy giftcode thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function PUT(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const id = parseInt(params.id)

    if (isNaN(id)) {
      return NextResponse.json(
        { error: 'ID không hợp lệ' },
        { status: 400 }
      )
    }

    const body = await request.json()
    const {
      code,
      server_id,
      gold,
      diamond,
      diamond_lock,
      items,
      expires_at,
      created_at,
    } = body

    const checkExistsSql = 'SELECT id, code FROM nr_gift_code WHERE id = ?'
    const existing = await query(checkExistsSql, [id])

    if (!existing || existing.length === 0) {
      return NextResponse.json(
        { error: 'Giftcode không tồn tại' },
        { status: 404 }
      )
    }

    const updateFields: string[] = []
    const updateParams: any[] = []

    if (code !== undefined) {
      if (code.trim() === '') {
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
      const codeUpper = code.toUpperCase().trim()
      if (codeUpper !== existing[0].code) {
        const checkCodeSql = 'SELECT id FROM nr_gift_code WHERE code = ? AND id != ?'
        const duplicate = await query(checkCodeSql, [codeUpper, id])
        if (duplicate && duplicate.length > 0) {
          return NextResponse.json(
            { error: `Code "${codeUpper}" đã tồn tại` },
            { status: 400 }
          )
        }
        updateFields.push('code = ?')
        updateParams.push(codeUpper)
      }
    }

    if (server_id !== undefined) {
      updateFields.push('server_id = ?')
      updateParams.push(server_id)
    }

    if (gold !== undefined) {
      updateFields.push('gold = ?')
      updateParams.push(gold)
    }

    if (diamond !== undefined) {
      updateFields.push('diamond = ?')
      updateParams.push(diamond)
    }

    if (diamond_lock !== undefined) {
      updateFields.push('diamond_lock = ?')
      updateParams.push(diamond_lock)
    }

    if (items !== undefined) {
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
      updateFields.push('items = ?')
      updateParams.push(itemsJson)
    }

    if (expires_at !== undefined) {
      updateFields.push('expires_at = ?')
      updateParams.push(new Date(expires_at))
    }

    if (created_at !== undefined) {
      updateFields.push('created_at = ?')
      updateParams.push(new Date(created_at))
    }

    if (updateFields.length === 0) {
      return NextResponse.json(
        { error: 'Không có trường nào để cập nhật' },
        { status: 400 }
      )
    }

    updateParams.push(id)

    const updateSql = `
      UPDATE nr_gift_code
      SET ${updateFields.join(', ')}
      WHERE id = ?
    `

    await query(updateSql, updateParams)

    return NextResponse.json({
      success: true,
      message: 'Đã cập nhật giftcode thành công',
    })
  } catch (error: any) {
    console.error('Update giftcode error:', error)
    if (error.code === 'ER_DUP_ENTRY') {
      return NextResponse.json(
        { error: 'Code đã tồn tại' },
        { status: 400 }
      )
    }
    return NextResponse.json(
      {
        error: 'Cập nhật giftcode thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

export async function DELETE(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const id = parseInt(params.id)

    if (isNaN(id)) {
      return NextResponse.json(
        { error: 'ID không hợp lệ' },
        { status: 400 }
      )
    }

    const checkExistsSql = 'SELECT id FROM nr_gift_code WHERE id = ?'
    const existing = await query(checkExistsSql, [id])

    if (!existing || existing.length === 0) {
      return NextResponse.json(
        { error: 'Giftcode không tồn tại' },
        { status: 404 }
      )
    }

    const deleteSql = 'DELETE FROM nr_gift_code WHERE id = ?'
    await query(deleteSql, [id])

    return NextResponse.json({
      success: true,
      message: 'Đã xóa giftcode thành công',
    })
  } catch (error) {
    console.error('Delete giftcode error:', error)
    return NextResponse.json(
      {
        error: 'Xóa giftcode thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


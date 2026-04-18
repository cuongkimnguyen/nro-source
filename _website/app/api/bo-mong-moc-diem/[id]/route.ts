import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function PUT(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const id = parseInt(params.id)
    if (isNaN(id) || id < 1) {
      return NextResponse.json(
        { error: 'ID không hợp lệ' },
        { status: 400 }
      )
    }

    const body = await request.json()
    const {
      diem_can_thiet,
      item_id_1,
      item_id_2,
      item_id_3,
      item_id_4,
      active,
      sort_order,
    } = body

    if (diem_can_thiet !== undefined && diem_can_thiet < 0) {
      return NextResponse.json(
        { error: 'Điểm cần thiết không hợp lệ' },
        { status: 400 }
      )
    }

    if (diem_can_thiet !== undefined) {
      const checkSql = 'SELECT id FROM nr_bo_mong_moc_diem WHERE diem_can_thiet = ? AND id != ?'
      const existing = await query(checkSql, [diem_can_thiet, id])
      
      if (existing && existing.length > 0) {
        return NextResponse.json(
          { error: `Mốc điểm ${diem_can_thiet} đã tồn tại` },
          { status: 400 }
        )
      }
    }

    const updateFields: string[] = []
    const updateValues: any[] = []

    if (diem_can_thiet !== undefined) {
      updateFields.push('diem_can_thiet = ?')
      updateValues.push(diem_can_thiet)
    }
    if (item_id_1 !== undefined) {
      updateFields.push('item_id_1 = ?')
      updateValues.push(item_id_1 || null)
    }
    if (item_id_2 !== undefined) {
      updateFields.push('item_id_2 = ?')
      updateValues.push(item_id_2 || null)
    }
    if (item_id_3 !== undefined) {
      updateFields.push('item_id_3 = ?')
      updateValues.push(item_id_3 || null)
    }
    if (item_id_4 !== undefined) {
      updateFields.push('item_id_4 = ?')
      updateValues.push(item_id_4 || null)
    }
    if (active !== undefined) {
      updateFields.push('active = ?')
      updateValues.push(active)
    }
    if (sort_order !== undefined) {
      updateFields.push('sort_order = ?')
      updateValues.push(sort_order)
    }

    if (updateFields.length === 0) {
      return NextResponse.json(
        { error: 'Không có trường nào để cập nhật' },
        { status: 400 }
      )
    }

    updateValues.push(id)

    const updateSql = `
      UPDATE nr_bo_mong_moc_diem
      SET ${updateFields.join(', ')}
      WHERE id = ?
    `

    await query(updateSql, updateValues)

    return NextResponse.json({
      success: true,
      message: 'Cập nhật mốc điểm thành công',
    })
  } catch (error: any) {
    console.error('Update bo mong moc diem error:', error)
    return NextResponse.json(
      {
        error: 'Cập nhật mốc điểm thất bại',
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
    if (isNaN(id) || id < 1) {
      return NextResponse.json(
        { error: 'ID không hợp lệ' },
        { status: 400 }
      )
    }

    const deleteSql = 'DELETE FROM nr_bo_mong_moc_diem WHERE id = ?'
    await query(deleteSql, [id])

    return NextResponse.json({
      success: true,
      message: 'Xóa mốc điểm thành công',
    })
  } catch (error) {
    console.error('Delete bo mong moc diem error:', error)
    return NextResponse.json(
      {
        error: 'Xóa mốc điểm thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

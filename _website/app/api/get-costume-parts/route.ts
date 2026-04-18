import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const { iconId } = body

    if (!iconId || iconId === '') {
      return NextResponse.json({ error: 'Vui lòng nhập ID ảnh cũ' }, { status: 400 })
    }

    const iconIdNum = parseInt(iconId)
    if (isNaN(iconIdNum) || iconIdNum < 1) {
      return NextResponse.json({ error: 'ID ảnh không hợp lệ' }, { status: 400 })
    }

    const getCostumeSql = 'SELECT id, name, head, body, leg FROM nr_item WHERE icon = ? AND type = 5 LIMIT 1'
    const costumeResult = await query(getCostumeSql, [iconIdNum])

    if (!costumeResult || costumeResult.length === 0) {
      return NextResponse.json({ error: `Không tìm thấy cải trang với icon ID ${iconIdNum}` }, { status: 404 })
    }

    const costume = costumeResult[0]
    const parts: { head: any[] | null; body: any[] | null; leg: any[] | null } = {
      head: null,
      body: null,
      leg: null,
    }

    const partIds = [
      { field: 'head', id: costume.head },
      { field: 'body', id: costume.body },
      { field: 'leg', id: costume.leg },
    ]

    for (const { field, id } of partIds) {
      if (id && id > 0) {
        const getPartSql = 'SELECT part FROM nr_part WHERE id = ?'
        const partResult = await query(getPartSql, [id])

        if (partResult && partResult.length > 0) {
          try {
            let partData = []
            if (partResult[0].part && typeof partResult[0].part === 'string') {
              partData = JSON.parse(partResult[0].part)
            } else if (Array.isArray(partResult[0].part)) {
              partData = partResult[0].part
            }
            parts[field as 'head' | 'body' | 'leg'] = partData
          } catch (parseError) {
            console.error(`Error parsing part ${field} for id ${id}:`, parseError)
          }
        }
      }
    }

    return NextResponse.json({
      success: true,
      costume: {
        id: costume.id,
        name: costume.name,
        head: costume.head,
        body: costume.body,
        leg: costume.leg,
      },
      parts,
    })
  } catch (error) {
    console.error('Get costume parts error:', error)
    return NextResponse.json(
      {
        error: 'Lấy part từ ID ảnh thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

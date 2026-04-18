import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = parseInt(params.id)

    if (isNaN(id) || id < 0) {
      return NextResponse.json({ error: 'ID không hợp lệ' }, { status: 400 })
    }

    const getPartSql = 'SELECT * FROM nr_part WHERE id = ?'
    const result = await query(getPartSql, [id])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { error: `Không tìm thấy part với ID ${id}` },
        { status: 404 }
      )
    }

    const part = result[0]
    
    // Parse the part JSON string
    let partData = []
    try {
      if (part.part && typeof part.part === 'string') {
        partData = JSON.parse(part.part)
      } else if (Array.isArray(part.part)) {
        partData = part.part
      }
    } catch (parseError) {
      console.error(`Error parsing part JSON for id ${id}:`, parseError, 'Raw part:', part.part)
      partData = []
    }
    
    part.part = partData

    return NextResponse.json({
      success: true,
      part,
    })
  } catch (error) {
    console.error('Get part error:', error)
    return NextResponse.json(
      {
        error: 'Lấy thông tin part thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}

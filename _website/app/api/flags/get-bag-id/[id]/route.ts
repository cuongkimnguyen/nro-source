import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

const ITEM_TO_BAG_MAPPING: Record<number, number> = {
  740: 72,
  741: 73,
  745: 74,
  800: 37,
  801: 38,
  802: 39,
  803: 40,
  804: 41,
  805: 42,
  814: 43,
  815: 44,
  816: 45,
  817: 46,
  822: 47,
  823: 48,
  852: 49,
  865: 50,
  966: 77,
  982: 78,
  983: 79,
  994: 81,
  995: 82,
  996: 83,
  997: 84,
  998: 85,
  999: 86,
  1000: 87,
  1001: 88,
  1007: 89,
  2021: 95,
  2041: 97,
  2105: 102,
  2115: 103,
  2132: 104,
  2133: 105,
  2136: 106,
  2228: 107,
}

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const itemId = parseInt(params.id)

    if (isNaN(itemId)) {
      return NextResponse.json(
        { error: 'Item ID không hợp lệ' },
        { status: 400 }
      )
    }

    const getItemSql = 'SELECT part FROM nr_item WHERE id = ? AND type = 11'
    const itemResult = await query(getItemSql, [itemId])

    if (itemResult && itemResult.length > 0) {
      const part = itemResult[0].part
      if (part !== null && part !== -1 && part >= 0) {
        return NextResponse.json({
          success: true,
          bagId: part,
          method: 'part_field',
        })
      }
    }

    if (ITEM_TO_BAG_MAPPING[itemId]) {
      return NextResponse.json({
        success: true,
        bagId: ITEM_TO_BAG_MAPPING[itemId],
        method: 'mapping',
      })
    }
    return NextResponse.json({
      success: false,
      message: 'Không tìm thấy bag ID cho item này. Vui lòng nhập thủ công.',
    })
  } catch (error) {
    console.error('Get bag ID error:', error)
    return NextResponse.json(
      {
        error: 'Lấy bag ID thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


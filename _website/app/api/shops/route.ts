import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const getShopConfigSql = "SELECT value FROM nr_others WHERE `key` = 'shop'"
    const result = await query(getShopConfigSql, [])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { error: 'Shop config không tồn tại' },
        { status: 404 }
      )
    }

    const shopConfig = JSON.parse(result[0].value)

    return NextResponse.json({
      success: true,
      shops: shopConfig,
    })
  } catch (error) {
    console.error('Get shops error:', error)
    return NextResponse.json(
      {
        error: 'Lấy danh sách shop thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


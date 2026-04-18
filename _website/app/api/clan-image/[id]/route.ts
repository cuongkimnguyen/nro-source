import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest, { params }: { params: { id: string } }) {
  try {
    const id = params.id

    const getClanImageSql = 'SELECT * FROM nr_clan_image WHERE id = ?'
    const result = await query(getClanImageSql, [id])

    if (!result || result.length === 0) {
      return NextResponse.json(
        { error: 'Clan image không tồn tại' },
        { status: 404 }
      )
    }

    const clanImage = result[0]
    let images: number[] = []
    
    try {
      images = JSON.parse(clanImage.images || '[]')
    } catch (error) {
      console.error('Parse images error:', error)
    }

    return NextResponse.json({
      success: true,
      clanImage: {
        ...clanImage,
        images,
      },
    })
  } catch (error) {
    console.error('Get clan image error:', error)
    return NextResponse.json(
      {
        error: 'Lấy thông tin clan image thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


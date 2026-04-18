import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url)
    const code = searchParams.get('code')

    if (!code || code.trim() === '') {
      return NextResponse.json(
        { error: 'Code không được để trống' },
        { status: 400 }
      )
    }

    const codeUpper = code.toUpperCase().trim()

    if (codeUpper.length < 5 || codeUpper.length > 30) {
      return NextResponse.json(
        { error: 'Code phải có độ dài từ 5 đến 30 ký tự' },
        { status: 400 }
      )
    }

    const getCodeSql = `
      SELECT 
        id, server_id, code, is_activated, gold, diamond, diamond_lock,
        items, expires_at, created_at
      FROM nr_gift_code
      WHERE code = ?
    `
    const codes = await query(getCodeSql, [codeUpper])

    if (!codes || codes.length === 0) {
      return NextResponse.json({
        success: true,
        exists: false,
        message: 'Code không tồn tại',
      })
    }

    const codeData = codes[0]
    const now = new Date()
    const expiresAt = new Date(codeData.expires_at)
    const createdAt = new Date(codeData.created_at)

    let status = 'active'
    let message = 'Code hợp lệ'
    
    if (now < createdAt) {
      status = 'not_started'
      message = 'Code chưa bắt đầu (chưa đến thời gian created_at)'
    } else if (now > expiresAt) {
      status = 'expired'
      message = 'Code đã hết hạn'
    }

    let itemsParsed: any[] = []
    try {
      itemsParsed = JSON.parse(codeData.items || '[]')
    } catch {}

    return NextResponse.json({
      success: true,
      exists: true,
      status,
      message,
      code: {
        id: codeData.id,
        code: codeData.code,
        server_id: codeData.server_id,
        gold: codeData.gold,
        diamond: codeData.diamond,
        diamond_lock: codeData.diamond_lock,
        items: itemsParsed,
        expires_at: codeData.expires_at,
        created_at: codeData.created_at,
      },
    })
  } catch (error) {
    console.error('Check giftcode error:', error)
    return NextResponse.json(
      {
        error: 'Kiểm tra giftcode thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


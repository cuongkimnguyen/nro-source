import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    const {
      table,
      item_id,
      tab,
      buy_gold = 0,
      buy_gem = 0,
      icon_special = 0,
      buy_special = 0,
      options = '[]',
      expired = -1,
      new: isNew = 0,
      preview = 0,
    } = body

    if (!table || !item_id || tab === undefined) {
      return NextResponse.json(
        { error: 'Thiếu thông tin bắt buộc: table, item_id, tab' },
        { status: 400 }
      )
    }

    const getShopConfigSql = "SELECT value FROM nr_others WHERE `key` = 'shop'"
    const shopConfigResult = await query(getShopConfigSql, [])
    
    if (!shopConfigResult || shopConfigResult.length === 0) {
      return NextResponse.json(
        { error: 'Shop config không tồn tại' },
        { status: 404 }
      )
    }

    const shopConfig = JSON.parse(shopConfigResult[0].value)
    const shop = shopConfig.find((s: any) => s.table === table)

    if (!shop) {
      return NextResponse.json(
        { error: `Shop table "${table}" không tồn tại trong config` },
        { status: 400 }
      )
    }

    if (tab < 0 || tab >= shop.tabs.length) {
      return NextResponse.json(
        { error: `Tab index ${tab} không hợp lệ. Shop có ${shop.tabs.length} tabs` },
        { status: 400 }
      )
    }

    const checkItemSql = 'SELECT id FROM nr_item WHERE id = ?'
    const itemResult = await query(checkItemSql, [item_id])
    
    if (!itemResult || itemResult.length === 0) {
      return NextResponse.json(
        { error: `Item ID ${item_id} không tồn tại trong nr_item` },
        { status: 400 }
      )
    }

    let optionsJson = '[]'
    if (options && options.trim() !== '') {
      try {
        const parsed = typeof options === 'string' ? JSON.parse(options) : options
        if (!Array.isArray(parsed)) {
          return NextResponse.json(
            { error: 'Options phải là một mảng JSON' },
            { status: 400 }
          )
        }
        optionsJson = JSON.stringify(parsed)
      } catch (err) {
        return NextResponse.json(
          { error: 'Options JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
          { status: 400 }
        )
      }
    }

    const shopType = shop.type || 0

    let insertSql = ''
    let insertParams: any[] = []

    if (shopType === 0 || shopType === 1) {
      insertSql = `
        INSERT INTO \`${table}\` (item_id, buy_gold, buy_gem, options, expired, \`new\`, preview, tab)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      `
      insertParams = [item_id, buy_gold, buy_gem, optionsJson, expired, isNew, preview, tab]
    } else if (shopType === 3) {
      insertSql = `
        INSERT INTO \`${table}\` (item_id, icon_special, buy_special, options, expired, \`new\`, preview, tab)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      `
      insertParams = [item_id, icon_special, buy_special, optionsJson, expired, isNew, preview, tab]
    } else {
      return NextResponse.json(
        { error: `Shop type ${shopType} không được hỗ trợ` },
        { status: 400 }
      )
    }

    try {
      await query(insertSql, insertParams)
    } catch (dbError: any) {
      if (dbError.code === 'ER_NO_SUCH_TABLE') {
        return NextResponse.json(
          { error: `Bảng "${table}" không tồn tại trong database` },
          { status: 400 }
        )
      }
      throw dbError
    }

    return NextResponse.json({
      success: true,
      message: `Đã thêm item ${item_id} vào shop "${table}" thành công`,
      data: {
        table,
        item_id,
        tab,
        shop_type: shopType,
      },
    })
  } catch (error) {
    console.error('Add shop item error:', error)
    return NextResponse.json(
      {
        error: 'Thêm shop item thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


import { NextRequest, NextResponse } from 'next/server'
import { query } from '@/lib/db'

export const runtime = 'nodejs'

export async function POST(request: NextRequest) {
  try {
    const requestBody = await request.json()
    const {
      name,
      description,
      icon,
      head = -1,
      bodyPart = -1,
      leg = -1,
      partHeadJson = '',
      partBodyJson = '',
      partLegJson = '',
      headAvatar = -1,
      gender = 3,
      arrHead2FramesData = '',
      arrHead2FramesCount = 1,
    } = requestBody

    if (!name || !description || !icon) {
      return NextResponse.json(
        { error: 'Vui lòng điền đầy đủ thông tin: tên, mô tả và icon' },
        { status: 400 }
      )
    }

    const maxIdSql = 'SELECT MAX(id) as maxId FROM nr_item'
    const maxIdResult = await query(maxIdSql, [])
    const maxId = maxIdResult && maxIdResult.length > 0 && maxIdResult[0].maxId 
      ? parseInt(maxIdResult[0].maxId) + 1 
      : 1
    
    const id = maxId

    const hasItemValues = (head > 0) || (bodyPart > 0) || (leg > 0)
    const hasPartValues = (partHeadJson && partHeadJson.trim() !== '') ||
                          (partBodyJson && partBodyJson.trim() !== '') ||
                          (partLegJson && partLegJson.trim() !== '')

    if (hasItemValues && hasPartValues) {
      return NextResponse.json(
        { error: 'Không thể điền cả nr_item và nr_part cùng lúc. Chọn một trong hai cách.' },
        { status: 400 }
      )
    }

    let finalHead = head > 0 ? head : -1
    let finalBody = bodyPart > 0 ? bodyPart : -1
    let finalLeg = leg > 0 ? leg : -1
    let partId = -1

    const defaultPartData = JSON.stringify([{ id: 0, dx: 0, dy: 0 }])

    // Kiểm tra có ArrHead2Frames không
    const hasArrHead2Frames = arrHead2FramesData && arrHead2FramesData.trim() !== ''
    let arrHead2FramesPartData: string[] = []
    let arrHead2FramesHeadIds: number[] = []
    if (hasArrHead2Frames) {
      try {
        arrHead2FramesPartData = JSON.parse(arrHead2FramesData)
        if (!Array.isArray(arrHead2FramesPartData)) {
          arrHead2FramesPartData = []
        }
      } catch {
        arrHead2FramesPartData = []
      }
    }

    if (hasPartValues) {
      const maxPartIdSql = 'SELECT MAX(id) as maxPartId FROM nr_part'
      const maxPartIdResult = await query(maxPartIdSql, [])
      const basePartId = maxPartIdResult && maxPartIdResult.length > 0 && maxPartIdResult[0].maxPartId 
        ? parseInt(maxPartIdResult[0].maxPartId) + 1 
        : 1

      let partHeadData = defaultPartData
      if (partHeadJson && partHeadJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partHeadJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Head JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partHeadData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Head JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      
      // Tạo head chính (từ partHeadJson)
      const insertPartHeadSql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 0, ?, ?)'
      await query(insertPartHeadSql, [basePartId, partHeadData, ''])
      finalHead = basePartId
      partId = basePartId
      
      // Tạo các head phụ từ ArrHead2Frames (nếu có)
      arrHead2FramesHeadIds = []
      for (let i = 0; i < arrHead2FramesPartData.length; i++) {
        const headId = basePartId + 1 + i
        const insertArrHeadSql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 0, ?, ?)'
        await query(insertArrHeadSql, [headId, arrHead2FramesPartData[i], ''])
        arrHead2FramesHeadIds.push(headId)
      }

      let partBodyData = defaultPartData
      if (partBodyJson && partBodyJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partBodyJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Body JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partBodyData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Body JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      const bodyPartId = basePartId + 1 + arrHead2FramesHeadIds.length
      const insertPartBodySql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 1, ?, ?)'
      await query(insertPartBodySql, [bodyPartId, partBodyData, ''])
      finalBody = bodyPartId

      let partLegData = defaultPartData
      if (partLegJson && partLegJson.trim() !== '') {
        try {
          const parsed = JSON.parse(partLegJson)
          if (!Array.isArray(parsed)) {
            return NextResponse.json(
              { error: 'Part Leg JSON phải là một mảng' },
              { status: 400 }
            )
          }
          partLegData = JSON.stringify(parsed)
        } catch (err) {
          return NextResponse.json(
            { error: 'Part Leg JSON không hợp lệ: ' + (err instanceof Error ? err.message : 'Lỗi không xác định') },
            { status: 400 }
          )
        }
      }
      const legPartId = basePartId + 2 + arrHead2FramesHeadIds.length
      const insertPartLegSql = 'INSERT INTO nr_part (id, type, part, note) VALUES (?, 2, ?, ?)'
      await query(insertPartLegSql, [legPartId, partLegData, ''])
      finalLeg = legPartId
    } else {
      partId = head > 0 ? head : -1
    }

    const insertSql = `
      INSERT INTO nr_item (
        id, name, type, gender, description, level, \`require\`,
        resale_price, icon, part, is_up_to_up,
        head, body, leg, options, mount_id, \`lock\`
      ) VALUES (?, ?, 5, ?, ?, 0, 0, -1, ?, ?, 0, ?, ?, ?, '[]', -1, 0)
    `

    await query(insertSql, [
      id,
      name,
      gender,
      description,
      icon,
      -1,
      finalHead,
      finalBody,
      finalLeg,
    ])

    if (headAvatar > 0) {
      try {
        const getAvatarSql = "SELECT value FROM nr_others WHERE `key` = 'avatar'"
        const avatarResult = await query(getAvatarSql, [])
        
        if (avatarResult && avatarResult.length > 0) {
          const currentValue = avatarResult[0].value
          let avatarArray = []
          
          try {
            avatarArray = JSON.parse(currentValue)
          } catch {
            avatarArray = []
          }
          
          const newMapping = {
            head: partId,
            avatar: headAvatar
          }
          
          const exists = avatarArray.some((item: any) => item.head === partId)
          if (!exists) {
            avatarArray.push(newMapping)
            const updatedValue = JSON.stringify(avatarArray)
            
            const updateAvatarSql = "UPDATE nr_others SET value = ? WHERE `key` = 'avatar'"
            await query(updateAvatarSql, [updatedValue])
          }
        } else {
          const newMapping = [{
            head: partId,
            avatar: headAvatar
          }]
          const insertAvatarSql = "INSERT INTO nr_others (`key`, value) VALUES ('avatar', ?)"
          await query(insertAvatarSql, [JSON.stringify(newMapping)])
        }
      } catch (error) {
        console.error('Error updating nr_others avatar:', error)
      }
    }

    // Insert ArrHead2Frames nếu có
    // Format mới: [id_head_chinh, id_head_phu1, id_head_phu2, ...] (KHÔNG còn id_ct)
    if (hasArrHead2Frames && hasPartValues && finalHead > 0) {
      try {
        // Bao gồm head chính và các head phụ
        const allHeadIds = [finalHead, ...arrHead2FramesHeadIds]
        // Lưu đúng kiểu hardcode: chỉ còn danh sách head
        const arrHead2FramesDataArray = allHeadIds
        const insertArrHead2FramesSql = 'INSERT INTO array_head_2_frames (data) VALUES (?)'
        await query(insertArrHead2FramesSql, [JSON.stringify(arrHead2FramesDataArray)])
        console.log('Inserted ArrHead2Frames:', JSON.stringify(arrHead2FramesDataArray))
      } catch (err) {
        console.error('Error inserting ArrHead2Frames:', err)
        // Không throw error, chỉ log để không làm gián đoạn việc tạo item
      }
    }

    const message = hasPartValues
      ? `Đã thêm cải trang "${name}" (ID: ${id}) thành công. Đã tạo part mới: Head=${finalHead}, Body=${finalBody}, Leg=${finalLeg}`
      : `Đã thêm cải trang "${name}" (ID: ${id}) thành công. Sử dụng part có sẵn: Head=${finalHead}, Body=${finalBody}, Leg=${finalLeg}`

    return NextResponse.json({
      success: true,
      message,
      data: {
        id,
        partId,
        name,
        description,
        icon,
        head: finalHead,
        body: finalBody,
        leg: finalLeg,
        partHeadJson,
        partBodyJson,
        partLegJson,
        headAvatar,
        gender,
      },
    })
  } catch (error) {
    console.error('Add costume error:', error)
    return NextResponse.json(
      {
        error: 'Thêm cải trang thất bại',
        details: error instanceof Error ? error.message : 'Lỗi không xác định',
      },
      { status: 500 }
    )
  }
}


'use client'

import { useEffect, useMemo, useState } from 'react'
import Button from '@/components/ui/Button'
import Popup from '@/components/ui/Popup'
import {
  UploadIcon,
  ImageIcon,
  CheckIcon,
  CheckCircleIcon,
  AlertCircleIcon,
  LoaderIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  SaveIcon,
  XIcon,
} from '@/components/ui/Icons'

type UploadResult = { fileName: string; imageId: number; paths: { [key: string]: string } }
type OldTriple = [number, number, number]
type OldObj = { id: number; dx: number; dy: number }
type NewObj = { id: number; dx: number; dy: number }
type Slot = 'head' | 'body' | 'leg' | 'icon' | 'headAvatar'
type PendingItem = { file: File; preview: string; oldId: number; slot: Slot | ''; isIcon?: boolean }
type LockedItem = { slot: Slot; file: File; preview: string; oldId: number; newId: number }
const slotMeta: Record<Slot, { label: string; color: string }> = {
  head: { label: 'Head', color: 'bg-blue-500' },
  body: { label: 'Body', color: 'bg-green-500' },
  leg: { label: 'Leg', color: 'bg-orange-500' },
  icon: { label: 'Icon', color: 'bg-purple-500' },
  headAvatar: { label: 'Head Avatar', color: 'bg-pink-500' },
}

function normalizeJson(text: string) {
  return text.trim().replace(/\r\n/g, '\n').replace(/^\uFEFF/, '')
}

function parsePartInput(text: string): OldObj[] {
  const normalized = normalizeJson(text)
  const parsed = JSON.parse(normalized)
  if (!Array.isArray(parsed)) throw new Error('Dữ liệu part phải là mảng')

  if (parsed.length === 0) return []

  const isTripleArray = Array.isArray(parsed[0])

  if (isTripleArray) {
    const triples: OldTriple[] = parsed.map((row: any, idx: number) => {
      if (!Array.isArray(row) || row.length !== 3) {
        throw new Error(`Phần tử #${idx + 1} phải là [id, dx, dy]`)
      }
      const [id, dx, dy] = row
      if (![id, dx, dy].every((v) => Number.isFinite(Number(v)))) {
        throw new Error(`Phần tử #${idx + 1} phải là số`)
      }
      return [Number(id), Number(dx), Number(dy)]
    })
    return triples.map(([id, dx, dy]) => ({ id, dx, dy }))
  }

  const objs: OldObj[] = parsed.map((row: any, idx: number) => {
    if (
      row === null ||
      typeof row !== 'object' ||
      !('id' in row) ||
      !('dx' in row) ||
      !('dy' in row)
    ) {
      throw new Error(`Phần tử #${idx + 1} phải chứa id, dx, dy`)
    }
    const id = Number(row.id)
    const dx = Number(row.dx)
    const dy = Number(row.dy)
    if (![id, dx, dy].every((v) => Number.isFinite(v))) {
      throw new Error(`Phần tử #${idx + 1} phải là số`)
    }
    return { id, dx, dy }
  })

  return objs
}

function convertPart(part: OldObj[], oldStart: number, newStart: number): NewObj[] {
  const delta = newStart - oldStart
  return part.map((p) => {
    if (p.id === 2955) return { id: 2955, dx: 0, dy: 0 }
    return { ...p, id: p.id + delta }
  })
}

function prettyJson(value: any) {
  return JSON.stringify(value, null, 2)
}

function parseOldIdFromFileName(fileName: string): number | null {
  const m1 = fileName.match(/small\s*0*([0-9]{1,5})/i)
  if (m1?.[1]) return parseInt(m1[1])
  const m2 = fileName.match(/([0-9]{1,5})(?=\D*$)/)
  if (m2?.[1]) return parseInt(m2[1])
  return null
}

function parseIdInput(value: string): number | null {
  const m = value.match(/(\d{1,6})/)
  if (!m) return null
  const num = parseInt(m[1])
  if (isNaN(num)) return null
  return num
}

function parsePartIdsInput(raw: string): number[] {
  if (!raw.trim().startsWith('[')) {
    const v = parseIdInput(raw)
    return v ? [v] : []
  }
  try {
    const parsed = JSON.parse(raw)
    const flat: any[] = Array.isArray(parsed) ? parsed.flat(2) : []
    const nums = flat
      .map((v) => {
        if (Array.isArray(v) && typeof v[0] === 'number') return v[0]
        if (v && typeof v.id === 'number') return v.id
        if (typeof v === 'number') return v
        return null
      })
      .filter((v) => v !== null) as number[]
    return nums
  } catch {
    const v = parseIdInput(raw)
    return v ? [v] : []
  }
}

function convertPartByMap(part: OldObj[], idMap: Map<number, number>) {
  const missing = new Set<number>()
  const converted: NewObj[] = part.map((p) => {
    if (p.id === 2955) return { id: 2955, dx: 0, dy: 0 }
    const mapped = idMap.get(p.id)
    if (!mapped) {
      missing.add(p.id)
      return { ...p, id: p.id }
    }
    return { ...p, id: mapped }
  })
  return { converted, missing: Array.from(missing).sort((a, b) => a - b) }
}

export default function AddCostumeMixPage() {
  const [step, setStep] = useState(1)

  const [startId, setStartId] = useState<string>('')
  const [verified, setVerified] = useState(false)
  const [checkedIds, setCheckedIds] = useState(false)
  const [uploadResults, setUploadResults] = useState<UploadResult[]>([])

  const [pendingFiles, setPendingFiles] = useState<PendingItem[]>([])
  const [activeSlot, setActiveSlot] = useState<Slot | ''>('')
  const [uploading, setUploading] = useState(false)
  const [classifying, setClassifying] = useState(false)
  const [partHeadOldId, setPartHeadOldId] = useState<string>('')
  const [partBodyOldId, setPartBodyOldId] = useState<string>('')
  const [partLegOldId, setPartLegOldId] = useState<string>('')
  
  // ArrHead2Frames states
  const [arrHead2FramesEnabled, setArrHead2FramesEnabled] = useState(false)
  const [arrHead2FramesOldInputs, setArrHead2FramesOldInputs] = useState<string[]>(['']) // Input cho từng head
  const [arrHead2FramesCount, setArrHead2FramesCount] = useState(1) // Số lượng head muốn tạo
  const [locked, setLocked] = useState<LockedItem[]>([])
  const [idMap, setIdMap] = useState<Map<number, number>>(new Map())
  const [lockError, setLockError] = useState<string | null>(null)

  const [partInput, setPartInput] = useState('[[15868,-2,-27],[15871,-2,-25],[2955,0,0]]')
  const [oldStartId, setOldStartId] = useState<string>('')
  const [newStartId, setNewStartId] = useState<string>('')
  const [partError, setPartError] = useState<string | null>(null)
  const [convertedPart, setConvertedPart] = useState<string>('')

  const [oldHeadInput, setOldHeadInput] = useState<string>('')
  const [oldBodyInput, setOldBodyInput] = useState<string>('')
  const [oldLegInput, setOldLegInput] = useState<string>('')
  const [newHeadOutput, setNewHeadOutput] = useState<string>('')
  const [newBodyOutput, setNewBodyOutput] = useState<string>('')
  const [newLegOutput, setNewLegOutput] = useState<string>('')
  const [genError, setGenError] = useState<string | null>(null)

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    icon: '',
    head: '-1',
    body: '-1',
    leg: '-1',
    partHeadJson: '',
    partBodyJson: '',
    partLegJson: '',
    headAvatar: '-1',
    gender: '3',
    arrHead2FramesData: '', // JSON string của converted ArrHead2Frames
    arrHead2FramesCount: 1, // Số lượng head
  })
  const [partJsonErrors, setPartJsonErrors] = useState<{ partHead: string | null; partBody: string | null; partLeg: string | null }>({
    partHead: null,
    partBody: null,
    partLeg: null,
  })
  const [submitting, setSubmitting] = useState(false)
  const [checkingImage, setCheckingImage] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)

  useEffect(() => {
    if (uploadResults.length > 0 && uploadResults[0].imageId) {
      setNewStartId(uploadResults[0].imageId.toString())
      setFormData((prev) => ({ ...prev, icon: uploadResults[0].imageId.toString() }))
    }
  }, [uploadResults])

  const handleAddFiles = async (files: File[]) => {
    const rows: PendingItem[] = []
    for (const file of files) {
      const oldId = parseOldIdFromFileName(file.name)
      if (!oldId) continue
      const preview = await new Promise<string>((resolve) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result as string)
        reader.readAsDataURL(file)
      })
      rows.push({ file, preview, oldId, slot: '', isIcon: false })
    }
    if (!rows.length) {
      setPopup({ message: 'Tên file không chứa ID hợp lệ (vd: Small12000.png)', type: 'error' })
      return
    }
    setPendingFiles((prev) => [...prev, ...rows])
    setLocked([])
    setIdMap(new Map())
    setVerified(false)
    setCheckedIds(false)
    setLockError(null)
  }

  const handleCheckIds = async () => {
    if (!startId.trim()) {
      setPopup({ message: 'Nhập ID bắt đầu', type: 'error' })
      return
    }
    const core = pendingFiles.filter((p) => p.slot === 'head' || p.slot === 'body' || p.slot === 'leg')
    if (!core.length) {
      setPopup({ message: 'Chọn ảnh head/body/leg và slot trước', type: 'error' })
      return
    }
    const startIdNum = parseInt(startId)
    if (isNaN(startIdNum) || startIdNum < 1 || startIdNum > 32000) {
      setPopup({ message: 'ID bắt đầu phải từ 1 đến 32000', type: 'error' })
      return
    }
    setVerified(false)
    setCheckedIds(false)
    try {
      const icon = pendingFiles.find((p) => p.isIcon)
      const headAvatar = pendingFiles.find((p) => p.slot === 'headAvatar')
      const uniqueOldIds = new Set<number>()
      core.forEach((p) => uniqueOldIds.add(p.oldId))
      if (icon && !uniqueOldIds.has(icon.oldId)) uniqueOldIds.add(icon.oldId)
      if (headAvatar && !uniqueOldIds.has(headAvatar.oldId)) uniqueOldIds.add(headAvatar.oldId)
      const totalCount = uniqueOldIds.size

      const response = await fetch('/api/check-ids', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ startId: startIdNum, count: totalCount }),
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Kiểm tra thất bại')
      setVerified(data.available)
      if (data.available) {
        setPopup({ message: `ID ${data.startId} → ${data.endId} khả dụng`, type: 'success' })
        const sorted = [...core].sort((a, b) => a.oldId - b.oldId)
        const map = new Map<number, number>()
        let nextIdx = 0
        const lockedRows: LockedItem[] = []
        sorted.forEach((row) => {
          const newId = startIdNum + nextIdx
          map.set(row.oldId, newId)
          lockedRows.push({ slot: row.slot as Slot, file: row.file, preview: row.preview, oldId: row.oldId, newId })
          nextIdx++
        })
        if (icon) {
          const iconNewId = map.has(icon.oldId) ? map.get(icon.oldId)! : startIdNum + nextIdx
          if (!map.has(icon.oldId)) {
            map.set(icon.oldId, iconNewId)
            nextIdx++
          }
          lockedRows.push({ slot: 'icon', file: icon.file, preview: icon.preview, oldId: icon.oldId, newId: iconNewId })
        }
        if (headAvatar) {
          const headAvatarNewId = map.has(headAvatar.oldId) ? map.get(headAvatar.oldId)! : startIdNum + nextIdx
          if (!map.has(headAvatar.oldId)) {
            map.set(headAvatar.oldId, headAvatarNewId)
            nextIdx++
          }
          lockedRows.push({ slot: 'headAvatar', file: headAvatar.file, preview: headAvatar.preview, oldId: headAvatar.oldId, newId: headAvatarNewId })
        }
        setLocked(lockedRows)
        setIdMap(map)
        setLockError(null)
      } else {
        setPopup({ message: `Trùng ID: ${data.conflicts.join(', ')}`, type: 'error' })
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Kiểm tra thất bại'
      setPopup({ message: msg, type: 'error' })
      setVerified(false)
    } finally {
      setCheckedIds(true)
    }
  }

  const handleClassifyByParts = async () => {
    const headIds = parsePartIdsInput(partHeadOldId.trim())
    const bodyIds = parsePartIdsInput(partBodyOldId.trim())
    const legIds = parsePartIdsInput(partLegOldId.trim())

    const arrHead2FramesIds: number[] = []
    if (arrHead2FramesEnabled && arrHead2FramesOldInputs.some((inp) => inp.trim())) {
      try {
        for (const input of arrHead2FramesOldInputs) {
          if (!input.trim()) continue
          const partData = parsePartInput(input)
          for (const part of partData) {
            if (part.id !== 2955) {
              arrHead2FramesIds.push(part.id)
            }
          }
        }
      } catch (e) {
        setPopup({ message: 'Lỗi parse ArrHead2Frames cũ: ' + (e instanceof Error ? e.message : 'Unknown'), type: 'error' })
        setClassifying(false)
        return
      }
    }

    if (!headIds.length && !bodyIds.length && !legIds.length && arrHead2FramesIds.length === 0) {
      setPopup({ message: 'Nhập part cũ (head/body/leg) hoặc ArrHead2Frames cũ trước', type: 'error' })
      return
    }
    if (pendingFiles.length === 0) {
      setPopup({ message: 'Chọn ảnh trước khi phân loại', type: 'error' })
      return
    }

    setClassifying(true)
    try {
      const headSet = new Set([...headIds, ...arrHead2FramesIds])
      const bodySet = new Set(bodyIds)
      const legSet = new Set(legIds)

      let matched = 0
      const nextPending = pendingFiles.map((p): PendingItem => {
        if (p.isIcon || p.slot === 'headAvatar') return p
        if (headSet.has(p.oldId)) {
          matched++
          return { ...p, slot: 'head' }
        }
        if (bodySet.has(p.oldId)) {
          matched++
          return { ...p, slot: 'body' }
        }
        if (legSet.has(p.oldId)) {
          matched++
          return { ...p, slot: 'leg' }
        }
        return { ...p, slot: '' }
      })
      setPendingFiles(nextPending)

      setLocked([])
      setIdMap(new Map())
      setCheckedIds(false)
      setVerified(false)

      if (matched === 0) {
        setPopup({ message: 'Không khớp được ảnh nào với part cũ', type: 'error' })
      } else {
        const arrHead2FramesCount = arrHead2FramesIds.length > 0 ? ` (${arrHead2FramesIds.length} ID từ ArrHead2Frames)` : ''
        setPopup({ message: `Đã phân loại ${matched} ảnh theo part cũ${arrHead2FramesCount}`, type: 'success' })
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Phân loại thất bại'
      setPopup({ message: msg, type: 'error' })
    } finally {
      setClassifying(false)
    }
  }

  const uploadLockedAll = async () => {
    if (!locked.length) {
      setPopup({ message: 'Chưa lock ID', type: 'error' })
      return
    }


    setUploading(true)
    const results: UploadResult[] = []
    const errors: string[] = []

    const enqueue = async (file: File, newId: number, apply: () => void) => {
      try {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('fileIndex', '0')
        formData.append('startId', newId.toString())
        const response = await fetch('/api/upload', { method: 'POST', body: formData })
        const data = await response.json()
        if (!response.ok) throw new Error(data.error || 'Upload thất bại')
        results.push({ fileName: file.name, imageId: newId, paths: data.paths || {} })
        apply()
      } catch (err) {
        errors.push(`${file.name}: ${err instanceof Error ? err.message : 'Upload lỗi'}`)
      }
    }

    for (const row of locked) {
      await enqueue(row.file, row.newId, () => {
        if (row.slot === 'icon') {
          setFormData((prev) => ({ ...prev, icon: row.newId.toString() }))
        } else if (row.slot === 'headAvatar') {
          setFormData((prev) => ({ ...prev, headAvatar: row.newId.toString() }))
        }
      })
    }

    setUploadResults((prev) => [...prev, ...results])
    setUploading(false)
    setCheckedIds(true)
    if (errors.length) {
      setPopup({ message: `${errors.length} lỗi: ${errors.slice(0, 3).join(', ')}${errors.length > 3 ? '...' : ''}`, type: 'error' })
    } else {
      setPopup({ message: `Upload xong ${results.length} ảnh`, type: 'success' })
      // tự gen part mới nếu có dữ liệu part cũ
      const applyAutoGen = () => {
        const convertText = (text: string) => {
          if (!text.trim()) return ''
          const part = parsePartInput(text)
          const { converted, missing } = convertPartByMap(part, idMap)
          if (missing.length) {
            setPopup({ message: `Thiếu mapping cho ID: ${missing.slice(0, 10).join(', ')}${missing.length > 10 ? '...' : ''}`, type: 'error' })
            throw new Error('missing mapping')
          }
          return prettyJson(converted)
        }
        let head = ''
        let body = ''
        let leg = ''
        try {
          head = convertText(partHeadOldId)
          body = convertText(partBodyOldId)
          leg = convertText(partLegOldId)
        } catch {
          return false
        }
        setNewHeadOutput(head)
        setNewBodyOutput(body)
        setNewLegOutput(leg)
        setFormData((prev) => ({
          ...prev,
          partHeadJson: head || prev.partHeadJson,
          partBodyJson: body || prev.partBodyJson,
          partLegJson: leg || prev.partLegJson,
        }))
        return true
      }
      const ok = applyAutoGen()
      if (ok) {
        // Convert ArrHead2Frames nếu đã kích hoạt
        // ArrHead2Frames cũ format: Part data của từng head (giống part head cũ)
        // Ví dụ: [[14386,-6,-17],[14388,-7,-18],[2955,0,0]]
        // Sau convert sẽ tạo nhiều head trong nr_part, rồi lưu các ID head vào array_head_2_frames
        if (arrHead2FramesEnabled && arrHead2FramesOldInputs.some((inp) => inp.trim())) {
          try {
            const convertedPartData: string[] = []
            for (const input of arrHead2FramesOldInputs) {
              if (!input.trim()) continue
              // Parse như part head cũ
              const partData = parsePartInput(input)
              if (partData.length === 0) continue
              
              // Convert part data sang ID mới
              const { converted, missing } = convertPartByMap(partData, idMap)
              if (missing.length) {
                throw new Error(`Thiếu mapping cho ID: ${missing.slice(0, 10).join(', ')}${missing.length > 10 ? '...' : ''}`)
              }
              
              // Lưu part data đã convert
              convertedPartData.push(prettyJson(converted))
            }
            
            // Lưu vào formData để dùng khi submit
            // Mỗi part data sẽ tạo 1 head trong nr_part
            if (convertedPartData.length > 0) {
              setFormData((prev) => ({
                ...prev,
                arrHead2FramesData: JSON.stringify(convertedPartData), // Lưu các part data đã convert
                arrHead2FramesCount: convertedPartData.length,
              }))
              // Ẩn input sau khi convert
              setArrHead2FramesOldInputs([''])
            }
          } catch (e) {
            console.error('Error converting ArrHead2Frames:', e)
            setPopup({ message: 'Lỗi convert ArrHead2Frames: ' + (e instanceof Error ? e.message : 'Unknown'), type: 'error' })
          }
        }
        setStep(3)
      } else {
        setStep(2)
      }
    }
  }

  const convertedInfo = useMemo(() => {
    if (!partInput.trim() || !oldStartId.trim() || !newStartId.trim()) {
      setPartError(null)
      return ''
    }
    try {
      const part = parsePartInput(partInput)
      const oldStart = parseInt(oldStartId)
      const newStart = parseInt(newStartId)
      if ([oldStart, newStart].some((v) => isNaN(v))) throw new Error('ID phải là số')
      const converted = convertPart(part, oldStart, newStart)
      const output = prettyJson(converted)
      setPartError(null)
      setConvertedPart(output)
      return output
    } catch (e) {
      const msg = e instanceof Error ? e.message : 'Dữ liệu không hợp lệ'
      setPartError(msg)
      setConvertedPart('')
      return ''
    }
  }, [partInput, oldStartId, newStartId])

  const handleCopyConverted = async () => {
    if (!convertedPart) return
    try {
      await navigator.clipboard.writeText(convertedPart)
      setPopup({ message: 'Đã copy part mới', type: 'success' })
    } catch {
      setPopup({ message: 'Copy thất bại', type: 'error' })
    }
  }

  useEffect(() => {
  }, [uploadResults])

  const handleGenByBase = () => {
    setGenError(null)
    try {
      if (!locked.length || idMap.size === 0) throw new Error('Chưa lock ID ở bước 1')

      const convertText = (text: string) => {
        if (!text.trim()) return ''
        const part = parsePartInput(text)
        const { converted, missing } = convertPartByMap(part, idMap)
        if (missing.length) {
          throw new Error(`Thiếu mapping cho ID: ${missing.slice(0, 10).join(', ')}${missing.length > 10 ? '...' : ''}`)
        }
        return prettyJson(converted)
      }

      const head = convertText(oldHeadInput)
      const body = convertText(oldBodyInput)
      const leg = convertText(oldLegInput)

      setNewHeadOutput(head)
      setNewBodyOutput(body)
      setNewLegOutput(leg)

      if (!head && !body && !leg) {
        throw new Error('Nhập ít nhất 1 part (head/body/leg)')
      }

      setPopup({ message: 'Gen xong', type: 'success' })
    } catch (e) {
      const msg = e instanceof Error ? e.message : 'Gen thất bại'
      setGenError(msg)
      setNewHeadOutput('')
      setNewBodyOutput('')
      setNewLegOutput('')
    }
  }

  const handleUseGenPart = (field: 'partHeadJson' | 'partBodyJson' | 'partLegJson', value: string) => {
    if (!value) return
    setFormData((prev) => ({ ...prev, [field]: value, head: '-1', body: '-1', leg: '-1' }))
    setPartJsonErrors((prev) => ({ ...prev, [field.replace('Json', '')]: null }))
    setPopup({ message: 'Đã điền vào bước 3', type: 'success' })
  }

  const handleFillPart = (field: 'partHeadJson' | 'partBodyJson' | 'partLegJson') => {
    if (!convertedPart) return
    setFormData((prev) => ({ ...prev, [field]: convertedPart }))
    setPartJsonErrors((prev) => ({ ...prev, [field.replace('Json', '') as 'partHead' | 'partBody' | 'partLeg']: null }))
  }

  const hasPartValues = useMemo(() => {
    return (
      formData.partHeadJson.trim() !== '' ||
      formData.partBodyJson.trim() !== '' ||
      formData.partLegJson.trim() !== ''
    )
  }, [formData.partHeadJson, formData.partBodyJson, formData.partLegJson])

  const hasItemValues = useMemo(() => {
    return (
      (formData.head !== '-1' && formData.head !== '') ||
      (formData.body !== '-1' && formData.body !== '') ||
      (formData.leg !== '-1' && formData.leg !== '')
    )
  }, [formData.head, formData.body, formData.leg])

  const handleChangeForm = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => {
      const newData = { ...prev, [name]: value }
      if (name === 'head' || name === 'body' || name === 'leg') {
        const hasItem = (newData.head !== '-1' && newData.head !== '') || (newData.body !== '-1' && newData.body !== '') || (newData.leg !== '-1' && newData.leg !== '')
        if (hasItem) {
          newData.partHeadJson = ''
          newData.partBodyJson = ''
          newData.partLegJson = ''
          setPartJsonErrors({ partHead: null, partBody: null, partLeg: null })
        }
      }
      if (name === 'partHeadJson' || name === 'partBodyJson' || name === 'partLegJson') {
        const hasPart = newData.partHeadJson.trim() !== '' || newData.partBodyJson.trim() !== '' || newData.partLegJson.trim() !== ''
        if (hasPart) {
          newData.head = '-1'
          newData.body = '-1'
          newData.leg = '-1'
        }
        if (value.trim() !== '') {
          try {
            const parsed = JSON.parse(value)
            if (!Array.isArray(parsed)) {
              setPartJsonErrors((prev) => ({ ...prev, [name.replace('Json', '')]: 'Phải là mảng JSON' }))
            } else {
              setPartJsonErrors((prev) => ({ ...prev, [name.replace('Json', '')]: null }))
            }
          } catch {
            setPartJsonErrors((prev) => ({ ...prev, [name.replace('Json', '')]: 'JSON không hợp lệ' }))
          }
        } else {
          setPartJsonErrors((prev) => ({ ...prev, [name.replace('Json', '')]: null }))
        }
      }
      return newData
    })
    if (name === 'icon') {
      setCheckingImage(false)
    }
  }

  const handleCheckImage = async () => {
    if (!formData.icon) {
      setPopup({ message: 'Nhập ID ảnh trước', type: 'error' })
      return
    }
    const imageId = parseInt(formData.icon)
    if (isNaN(imageId) || imageId < 1) {
      setPopup({ message: 'ID ảnh không hợp lệ', type: 'error' })
      return
    }
    setCheckingImage(true)
    try {
      const response = await fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId }),
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Kiểm tra ảnh thất bại')
      if (data.exists) {
        setPopup({ message: data.message, type: 'success' })
      } else {
        setPopup({ message: data.message, type: 'error' })
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Kiểm tra ảnh thất bại'
      setPopup({ message: msg, type: 'error' })
    } finally {
      setCheckingImage(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!formData.name || !formData.description || !formData.icon) {
      setPopup({ message: 'Điền đủ tên, mô tả, icon', type: 'error' })
      return
    }
    const icon = parseInt(formData.icon)
    if (isNaN(icon) || icon < 0) {
      setPopup({ message: 'Icon phải là số nguyên dương', type: 'error' })
      return
    }
    if (hasPartValues) {
      const fields: Array<'partHeadJson' | 'partBodyJson' | 'partLegJson'> = ['partHeadJson', 'partBodyJson', 'partLegJson']
      for (const field of fields) {
        const val = formData[field].trim()
        if (val) {
          try {
            const parsed = JSON.parse(val)
            if (!Array.isArray(parsed)) {
              setPopup({ message: `${field} phải là mảng`, type: 'error' })
              return
            }
          } catch {
            setPopup({ message: `${field} không hợp lệ`, type: 'error' })
            return
          }
        }
      }
    }
    setSubmitting(true)
    try {
      const response = await fetch('/api/add-costume', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name,
          description: formData.description,
          icon,
          head: parseInt(formData.head) || -1,
          bodyPart: parseInt(formData.body) || -1,
          leg: parseInt(formData.leg) || -1,
          partHeadJson: formData.partHeadJson.trim(),
          partBodyJson: formData.partBodyJson.trim(),
          partLegJson: formData.partLegJson.trim(),
          headAvatar: parseInt(formData.headAvatar) || -1,
          gender: parseInt(formData.gender) || 3,
          arrHead2FramesData: formData.arrHead2FramesData || '',
          arrHead2FramesCount: formData.arrHead2FramesCount || 1,
        }),
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Thêm cải trang thất bại')
      setPopup({ message: data.message, type: 'success' })
      setFormData({
        name: '',
        description: '',
        icon: '',
        head: '-1',
        body: '-1',
        leg: '-1',
        partHeadJson: '',
        partBodyJson: '',
        partLegJson: '',
        headAvatar: '-1',
        gender: '3',
        arrHead2FramesData: '',
        arrHead2FramesCount: 1,
      })
      setArrHead2FramesEnabled(false)
      setArrHead2FramesOldInputs([''])
      setStep(1)
      setUploadResults([])
      setConvertedPart('')
      setPartInput('')
      setOldStartId('')
      setNewStartId('')
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Thêm cải trang thất bại'
      setPopup({ message: msg, type: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  const canGoStep2 = checkedIds || uploadResults.length > 0

  return (
    <>
      {popup && <Popup message={popup.message} type={popup.type} onClose={() => setPopup(null)} />}
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-6xl mx-auto space-y-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <SaveIcon className="w-6 h-6 text-purple-600" />
            </div>
            <div className="flex-1">
              <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Cải Trang Mix</h1>
              <p className="text-sm text-gray-600">Upload ảnh mới → chuyển part → thêm cải trang</p>
            </div>
            <div className="flex gap-2 text-sm">
              <span className={`px-3 py-1 rounded-full ${step === 1 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-700'}`}>1. Upload</span>
              <span className={`px-3 py-1 rounded-full ${step === 2 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-700'}`}>2. Part</span>
              <span className={`px-3 py-1 rounded-full ${step === 3 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-700'}`}>3. Item</span>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-lg">
                <UploadIcon className="w-6 h-6 text-blue-600" />
              </div>
              <div className="flex-1">
                <h2 className="text-xl md:text-2xl font-bold text-gray-900">Bước 1: Chọn ảnh + Lock ID</h2>
                <p className="text-sm text-gray-500">Head / Body / Leg (nhiều ảnh) + Icon/Head Avatar (lẻ)</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <ImageIcon className="w-5 h-5 text-blue-600 mt-0.5" />
                  <div className="flex-1">
                    <label className="block text-sm font-medium text-gray-700 mb-2">ID Ảnh Bắt Đầu</label>
                    <div className="flex gap-2">
                      <input
                        type="number"
                        min="1"
                        max="32000"
                        value={startId}
                        onChange={(e) => {
                          setStartId(e.target.value)
                          setVerified(false)
                        }}
                        placeholder="Ví dụ: 22001"
                        className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      />
                      <Button onClick={handleCheckIds} variant="outline" className="min-w-[110px]">
                        <CheckIcon className="w-4 h-4 mr-2" />
                        Kiểm tra
                      </Button>
                    </div>
                    <p className="mt-2 text-xs text-gray-600">Check xong sẽ lock mapping old→new theo thứ tự tăng dần.</p>
                  </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-2 mt-3">
                  <div>
                    <label className="block text-xs text-gray-600 mb-1">Part Head cũ</label>
                    <input
                      type="text"
                      value={partHeadOldId}
                      onChange={(e) => setPartHeadOldId(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="ID part head (dán chuỗi cũng được)"
                    />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-600 mb-1">Part Body cũ</label>
                    <input
                      type="text"
                      value={partBodyOldId}
                      onChange={(e) => setPartBodyOldId(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="ID part body (dán chuỗi cũng được)"
                    />
                  </div>
                  <div>
                    <label className="block text-xs text-gray-600 mb-1">Part Leg cũ</label>
                    <input
                      type="text"
                      value={partLegOldId}
                      onChange={(e) => setPartLegOldId(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="ID part leg (dán chuỗi cũng được)"
                    />
                  </div>
                </div>
                <div className="mt-2 flex gap-2 flex-wrap">
                  <Button type="button" size="sm" variant="outline" onClick={handleClassifyByParts} disabled={classifying}>
                    {classifying ? 'Đang phân loại...' : 'Phân loại theo part cũ'}
                  </Button>
                  <Button
                    type="button"
                    size="sm"
                    variant={arrHead2FramesEnabled ? 'default' : 'outline'}
                    onClick={() => {
                      setArrHead2FramesEnabled(!arrHead2FramesEnabled)
                      if (!arrHead2FramesEnabled) {
                        setArrHead2FramesOldInputs(['', ''])
                        setArrHead2FramesCount(2)
                      } else {
                        setArrHead2FramesOldInputs([''])
                        setArrHead2FramesCount(1)
                      }
                    }}
                  >
                    {arrHead2FramesEnabled ? '✓ Kích hoạt ArrHead2Frames' : 'Kích hoạt ArrHead2Frames'}
                  </Button>
                </div>
                {arrHead2FramesEnabled && (
                  <div className="mt-3 pt-3 border-t border-gray-200">
                    <div className="flex items-center justify-between mb-2">
                      <label className="block text-xs font-medium text-gray-700">
                        ArrHead2Frames cũ ({arrHead2FramesOldInputs.length} head - tối thiểu 2)
                      </label>
                      <Button
                        type="button"
                        size="sm"
                        variant="outline"
                        onClick={() => {
                          setArrHead2FramesOldInputs([...arrHead2FramesOldInputs, ''])
                        }}
                      >
                        + Thêm input head2
                      </Button>
                    </div>
                    {arrHead2FramesOldInputs.map((input, idx) => (
                      <div key={idx} className="mb-3">
                        <div className="flex items-center justify-between mb-1">
                          <label className="block text-xs text-gray-600">
                            Head {idx + 1} cũ (Part data - giống nhập Part Head cũ)
                          </label>
                          {arrHead2FramesOldInputs.length > 2 && (
                            <Button
                              type="button"
                              size="sm"
                              variant="ghost"
                              onClick={() => {
                                const newInputs = arrHead2FramesOldInputs.filter((_, i) => i !== idx)
                                if (newInputs.length < 2) {
                                  setPopup({ message: 'ArrHead2Frames cần tối thiểu 2 head', type: 'error' })
                                  return
                                }
                                setArrHead2FramesOldInputs(newInputs)
                              }}
                              className="text-red-600 hover:text-red-700"
                            >
                              Xóa
                            </Button>
                          )}
                        </div>
                        <textarea
                          value={input}
                          onChange={(e) => {
                            const newInputs = [...arrHead2FramesOldInputs]
                            newInputs[idx] = e.target.value
                            setArrHead2FramesOldInputs(newInputs)
                          }}
                          className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono"
                          placeholder="[[14386,-6,-17],[14388,-7,-18],[2955,0,0]]"
                          rows={3}
                        />
                        <p className="text-xs text-gray-500 mt-1">
                          Ví dụ: [[14386,-6,-17],[14388,-7,-18],[2955,0,0]] - Part data của head cũ (sẽ tự convert ID)
                        </p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <p className="text-sm font-medium text-gray-700 mb-2">Mapping đã lock</p>
                {lockError && <p className="text-sm text-red-600">{lockError}</p>}
                {!locked.length ? (
                  <p className="text-xs text-gray-500">Chưa lock. Chọn ảnh head/body/leg rồi bấm “Kiểm tra”.</p>
                ) : (
                  <div className="max-h-40 overflow-auto border border-gray-200 rounded-lg bg-white">
                    <table className="w-full text-xs">
                      <thead className="sticky top-0 bg-gray-50">
                        <tr>
                          <th className="text-left p-2">Slot</th>
                          <th className="text-left p-2">Old</th>
                          <th className="text-left p-2">New</th>
                          <th className="text-left p-2">File</th>
                        </tr>
                      </thead>
                      <tbody>
                        {locked.map((r, i) => (
                          <tr key={i} className="border-t">
                            <td className="p-2">{r.slot}</td>
                            <td className="p-2">{r.oldId}</td>
                            <td className="p-2 font-semibold">{r.newId}</td>
                            <td className="p-2 truncate max-w-[180px]">{r.file.name}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>

            <div className="border border-dashed border-gray-300 rounded-lg p-4 mb-4">
              <div className="flex items-center justify-between mb-3">
                <div>
                  <p className="text-sm font-semibold text-gray-800">Chọn ảnh</p>
                  <p className="text-xs text-gray-500">Chọn type trước, rồi tick ảnh để gán type</p>
                </div>
                <div className="flex items-center gap-2">
                  <select
                    value={activeSlot}
                    onChange={(e) => setActiveSlot(e.target.value as Slot | '')}
                    className="px-3 py-2 border border-gray-300 rounded-lg text-sm"
                  >
                    <option value="">Select Type</option>
                    <option value="head">Head</option>
                    <option value="body">Body</option>
                    <option value="leg">Leg</option>
                    <option value="icon">Icon</option>
                    <option value="headAvatar">Head Avatar</option>
                  </select>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      const input = document.createElement('input')
                      input.type = 'file'
                      input.accept = 'image/*'
                      input.multiple = true
                      input.onchange = async (ev: any) => {
                        const files = Array.from(ev.target.files || []) as File[]
                        await handleAddFiles(files)
                      }
                      input.click()
                    }}
                  >
                    Chọn ảnh
                  </Button>
                </div>
              </div>

              {pendingFiles.length === 0 ? (
                <p className="text-xs text-gray-500">Chưa có ảnh</p>
              ) : (
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                  {pendingFiles.map((p, idx) => (
                    <div
                      key={idx}
                      className={`border rounded-lg p-2 bg-white cursor-pointer select-none ${
                        p.slot ? 'border-gray-300' : 'border-gray-200'
                      }`}
                      onClick={() => {
                        if (!activeSlot) {
                          setPopup({ message: 'Chọn type trước', type: 'error' })
                          return
                        }
                        setPendingFiles((prev) => {
                          const next = [...prev]
                          const cur = next[idx]

                          if (activeSlot === 'icon') {
                            const willSelect = !cur.isIcon
                            if (willSelect) {
                              for (let i = 0; i < next.length; i++) {
                                if (i !== idx && next[i].isIcon) next[i] = { ...next[i], isIcon: false }
                              }
                            }
                            next[idx] = { ...cur, isIcon: !cur.isIcon }
                            return next
                          }

                          const nextSlot = cur.slot === activeSlot ? '' : activeSlot
                          if (nextSlot === 'headAvatar') {
                            for (let i = 0; i < next.length; i++) {
                              if (i !== idx && next[i].slot === 'headAvatar') {
                                next[i] = { ...next[i], slot: '' }
                              }
                            }
                          }
                          next[idx] = { ...cur, slot: nextSlot }
                          return next
                        })
                        setLocked([])
                        setIdMap(new Map())
                        setCheckedIds(false)
                        setVerified(false)
                      }}
                    >
                      <div className="relative mb-2">
                        <img src={p.preview} alt={p.file.name} className="w-full h-28 object-contain bg-gray-50 rounded" />
                        <span className="absolute top-1 left-1 bg-black/60 text-white text-[10px] px-2 py-0.5 rounded">
                          {p.oldId}
                        </span>
                        <button
                          type="button"
                          onClick={() => {
                          }}
                          className={`absolute top-1 right-1 w-7 h-7 rounded-full border-2 flex items-center justify-center text-white text-xs pointer-events-none ${
                            p.slot ? slotMeta[p.slot].color : 'bg-gray-400'
                          } ${!activeSlot ? 'opacity-60' : ''}`}
                          title="Tick"
                        >
                          {p.slot ? '✓' : ''}
                        </button>
                      </div>
                      <p className="text-[11px] font-medium truncate">{p.file.name}</p>
                        <div className="flex items-center justify-between mt-2">
                        <div className="text-[10px] text-gray-500 flex flex-col gap-0.5">
                          <span>Slot: {p.slot ? slotMeta[p.slot].label : 'Chưa chọn'}</span>
                          <span>Icon: {p.isIcon ? 'Có' : 'Không'}</span>
                        </div>
                          <button
                            type="button"
                            onClick={(ev) => {
                              ev.stopPropagation()
                              setPendingFiles((prev) => prev.filter((_, i) => i !== idx))
                              setLocked([])
                              setIdMap(new Map())
                              setCheckedIds(false)
                              setVerified(false)
                            }}
                            className="bg-red-600 hover:bg-red-700 text-white rounded px-2 py-1 text-[10px]"
                            title="Xóa"
                          >
                            Xóa
                          </button>
                        </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="flex items-center justify-end">
              <Button onClick={uploadLockedAll} disabled={!locked.length || uploading} size="lg">
                {uploading ? 'Đang upload...' : 'Upload theo ID đã lock'}
              </Button>
            </div>
          </div>

          <div className={`bg-white rounded-xl shadow-lg p-6 md:p-8 ${!canGoStep2 ? 'opacity-60 pointer-events-none' : ''}`}>
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-indigo-100 rounded-lg">
                <CheckCircleIcon className="w-6 h-6 text-indigo-600" />
              </div>
              <div className="flex-1">
                <h2 className="text-xl md:text-2xl font-bold text-gray-900">Bước 2: Chuyển part sang ID mới</h2>
                <p className="text-sm text-gray-500">So le theo base ID hoặc tự nhận diện từ database</p>
              </div>
              <div className="flex gap-2">
                <Button type="button" variant="outline" onClick={() => setStep(1)}>
                  <ChevronLeftIcon className="w-5 h-5 mr-1" />
                  Bước 1
                </Button>
                <Button type="button" onClick={() => setStep(3)} disabled={!newHeadOutput && !newBodyOutput && !newLegOutput}>
                  Bước 3
                  <ChevronRightIcon className="w-5 h-5 ml-1" />
                </Button>
              </div>
            </div>

            <div className="bg-white border border-gray-200 rounded-lg p-4 mb-6">
              <div className="flex items-start gap-3">
                <ImageIcon className="w-5 h-5 text-indigo-600 mt-0.5" />
                <div className="flex-1">
                  <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">Head (cũ)</label>
                      <textarea
                        value={oldHeadInput}
                        onChange={(e) => setOldHeadInput(e.target.value)}
                        rows={7}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg font-mono text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        placeholder='[[12000,0,0],[12001,0,0]] hoặc [{"id":12000,"dx":0,"dy":0}]'
                      />
                      <label className="text-sm font-medium text-gray-700">Head (mới)</label>
                      <textarea value={newHeadOutput} readOnly rows={7} className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-xs" />
                      <div className="flex gap-2">
                        <Button type="button" variant="outline" size="sm" onClick={async () => {
                          try {
                            await navigator.clipboard.writeText(newHeadOutput)
                            setPopup({ message: 'Đã copy Head', type: 'success' })
                          } catch {
                            setPopup({ message: 'Copy thất bại', type: 'error' })
                          }
                        }} disabled={!newHeadOutput} className="flex-1">Copy</Button>
                        <Button type="button" size="sm" onClick={() => handleUseGenPart('partHeadJson', newHeadOutput)} disabled={!newHeadOutput} className="flex-1">Dùng</Button>
                      </div>
                    </div>
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">Body (cũ)</label>
                      <textarea
                        value={oldBodyInput}
                        onChange={(e) => setOldBodyInput(e.target.value)}
                        rows={7}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg font-mono text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        placeholder='[[12000,0,0],[12001,0,0]] hoặc [{"id":12000,"dx":0,"dy":0}]'
                      />
                      <label className="text-sm font-medium text-gray-700">Body (mới)</label>
                      <textarea value={newBodyOutput} readOnly rows={7} className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-xs" />
                      <div className="flex gap-2">
                        <Button type="button" variant="outline" size="sm" onClick={async () => {
                          try {
                            await navigator.clipboard.writeText(newBodyOutput)
                            setPopup({ message: 'Đã copy Body', type: 'success' })
                          } catch {
                            setPopup({ message: 'Copy thất bại', type: 'error' })
                          }
                        }} disabled={!newBodyOutput} className="flex-1">Copy</Button>
                        <Button type="button" size="sm" onClick={() => handleUseGenPart('partBodyJson', newBodyOutput)} disabled={!newBodyOutput} className="flex-1">Dùng</Button>
                      </div>
                    </div>
                    <div className="space-y-2">
                      <label className="text-sm font-medium text-gray-700">Leg (cũ)</label>
                      <textarea
                        value={oldLegInput}
                        onChange={(e) => setOldLegInput(e.target.value)}
                        rows={7}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg font-mono text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                        placeholder='[[12000,0,0],[12001,0,0]] hoặc [{"id":12000,"dx":0,"dy":0}]'
                      />
                      <label className="text-sm font-medium text-gray-700">Leg (mới)</label>
                      <textarea value={newLegOutput} readOnly rows={7} className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 font-mono text-xs" />
                      <div className="flex gap-2">
                        <Button type="button" variant="outline" size="sm" onClick={async () => {
                          try {
                            await navigator.clipboard.writeText(newLegOutput)
                            setPopup({ message: 'Đã copy Leg', type: 'success' })
                          } catch {
                            setPopup({ message: 'Copy thất bại', type: 'error' })
                          }
                        }} disabled={!newLegOutput} className="flex-1">Copy</Button>
                        <Button type="button" size="sm" onClick={() => handleUseGenPart('partLegJson', newLegOutput)} disabled={!newLegOutput} className="flex-1">Dùng</Button>
                      </div>
                    </div>
                  </div>

                  {genError && (
                    <div className="mt-3 bg-red-50 border border-red-200 rounded-lg p-3 text-sm text-red-700 flex gap-2">
                      <AlertCircleIcon className="w-5 h-5 text-red-600 mt-0.5" />
                      <span>{genError}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>

          </div>

          <div className={`bg-white rounded-xl shadow-lg p-6 md:p-8 ${step < 2 ? 'opacity-60 pointer-events-none' : ''}`}>
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-green-100 rounded-lg">
                <SaveIcon className="w-6 h-6 text-green-600" />
              </div>
              <div className="flex-1">
                <h2 className="text-xl md:text-2xl font-bold text-gray-900">Bước 3: Thêm cải trang</h2>
                <p className="text-sm text-gray-500">Điền thông tin item. Có thể dán part mới vào Head/Body/Leg.</p>
              </div>
              <div className="flex gap-2">
                <Button type="button" variant="outline" onClick={() => setStep(2)}>
                  <ChevronLeftIcon className="w-5 h-5 mr-1" />
                  Bước 2
                </Button>
              </div>
            </div>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Icon (ID ảnh) <span className="text-red-500">*</span>
                </label>
                <div className="flex gap-2">
                  <input
                    type="number"
                    name="icon"
                    value={formData.icon}
                    onChange={handleChangeForm}
                    required
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="VD: 22001"
                  />
                  <Button type="button" onClick={handleCheckImage} disabled={!formData.icon || checkingImage} variant="outline" className="min-w-[110px]">
                    {checkingImage ? (
                      <>
                        <LoaderIcon className="w-4 h-4 mr-2 animate-spin" />
                        Đang kiểm
                      </>
                    ) : (
                      <>
                        <CheckCircleIcon className="w-4 h-4 mr-2" />
                        Kiểm tra
                      </>
                    )}
                  </Button>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tên Cải Trang <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChangeForm}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Ví dụ: Cải trang mix"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Giới Tính
                  </label>
                  <select
                    name="gender"
                    value={formData.gender}
                    onChange={handleChangeForm}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="0">Nam (0)</option>
                    <option value="1">Nữ (1)</option>
                    <option value="2">Xayda (2)</option>
                    <option value="3">Tất cả (3)</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Mô Tả <span className="text-red-500">*</span>
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChangeForm}
                  required
                  rows={3}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Mô tả..."
                />
              </div>

              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-sm text-gray-700">
                Chọn một trong hai: điền Head/Body/Leg (ID nr_part) hoặc dán Part JSON. Khi nhập Part JSON, hệ thống tự clear Head/Body/Leg.
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Head (ID) - nr_item</label>
                  <input
                    type="number"
                    name="head"
                    value={formData.head}
                    onChange={handleChangeForm}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : 'border-gray-300'}`}
                    placeholder="-1"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Body (ID) - nr_item</label>
                  <input
                    type="number"
                    name="body"
                    value={formData.body}
                    onChange={handleChangeForm}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : 'border-gray-300'}`}
                    placeholder="-1"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Leg (ID) - nr_item</label>
                  <input
                    type="number"
                    name="leg"
                    value={formData.leg}
                    onChange={handleChangeForm}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : 'border-gray-300'}`}
                    placeholder="-1"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="space-y-2">
                  <label className="block text-sm font-medium text-gray-700">Part Head JSON</label>
                  <textarea
                    name="partHeadJson"
                    value={formData.partHeadJson}
                    onChange={handleChangeForm}
                    disabled={hasItemValues}
                    rows={5}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''} ${partJsonErrors.partHead ? 'border-red-500' : 'border-gray-300'}`}
                    placeholder='[{"id":...,"dx":...,"dy":...}]'
                  />
                  <div className="flex gap-2">
                    <Button type="button" variant="outline" onClick={() => handleFillPart('partHeadJson')} disabled={!convertedPart || hasItemValues}>
                      Dán part mới
                    </Button>
                  </div>
                  {partJsonErrors.partHead && <p className="text-xs text-red-500">{partJsonErrors.partHead}</p>}
                </div>
                <div className="space-y-2">
                  <label className="block text-sm font-medium text-gray-700">Part Body JSON</label>
                  <textarea
                    name="partBodyJson"
                    value={formData.partBodyJson}
                    onChange={handleChangeForm}
                    disabled={hasItemValues}
                    rows={5}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''} ${partJsonErrors.partBody ? 'border-red-500' : 'border-gray-300'}`}
                    placeholder='[{"id":...,"dx":...,"dy":...}]'
                  />
                  <div className="flex gap-2">
                    <Button type="button" variant="outline" onClick={() => handleFillPart('partBodyJson')} disabled={!convertedPart || hasItemValues}>
                      Dán part mới
                    </Button>
                  </div>
                  {partJsonErrors.partBody && <p className="text-xs text-red-500">{partJsonErrors.partBody}</p>}
                </div>
                <div className="space-y-2">
                  <label className="block text-sm font-medium text-gray-700">Part Leg JSON</label>
                  <textarea
                    name="partLegJson"
                    value={formData.partLegJson}
                    onChange={handleChangeForm}
                    disabled={hasItemValues}
                    rows={5}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''} ${partJsonErrors.partLeg ? 'border-red-500' : 'border-gray-300'}`}
                    placeholder='[{"id":...,"dx":...,"dy":...}]'
                  />
                  <div className="flex gap-2">
                    <Button type="button" variant="outline" onClick={() => handleFillPart('partLegJson')} disabled={!convertedPart || hasItemValues}>
                      Dán part mới
                    </Button>
                  </div>
                  {partJsonErrors.partLeg && <p className="text-xs text-red-500">{partJsonErrors.partLeg}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Head Avatar</label>
                  <input
                    type="number"
                    name="headAvatar"
                    value={formData.headAvatar}
                    onChange={handleChangeForm}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="-1"
                  />
                </div>
                <div className="flex items-end justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setStep(2)}>
                    <ChevronLeftIcon className="w-5 h-5 mr-1" />
                    Quay lại
                  </Button>
                  <Button type="submit" disabled={submitting} className="min-w-[160px]">
                    {submitting ? (
                      <>
                        <LoaderIcon className="w-5 h-5 mr-2 animate-spin inline" />
                        Đang lưu...
                      </>
                    ) : (
                      <>
                        <SaveIcon className="w-5 h-5 mr-2 inline" />
                        Lưu cải trang
                      </>
                    )}
                  </Button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  )
}


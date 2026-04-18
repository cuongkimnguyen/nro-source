'use client'

import { useEffect, useState } from 'react'
import Button from '@/components/ui/Button'
import Popup from '@/components/ui/Popup'
import {
  UploadIcon,
  ImageIcon,
  CheckIcon,
  CheckCircleIcon,
  AlertCircleIcon,
  LoaderIcon,
  ChevronRightIcon,
  SaveIcon,
  CodeIcon,
} from '@/components/ui/Icons'

type UploadResult = { fileName: string; imageId: number; paths: { [key: string]: string } }
type Slot = 'icon' | 'effect'
type PendingItem = { file: File; preview: string; oldId: number; slot: Slot | '' }
type LockedItem = { slot: Slot; file: File; preview: string; oldId: number; newId: number }

const slotMeta: Record<Slot, { label: string; color: string }> = {
  icon: { label: 'Icon', color: 'bg-purple-500' },
  effect: { label: 'Effect', color: 'bg-blue-500' },
}

function parseOldIdFromFileName(fileName: string): number | null {
  const m1 = fileName.match(/small\s*0*([0-9]{1,5})/i)
  if (m1?.[1]) return parseInt(m1[1])
  const m2 = fileName.match(/([0-9]{1,5})(?=\D*$)/)
  if (m2?.[1]) return parseInt(m2[1])
  return null
}

export default function AddDanhHieuMixPage() {
  const [step, setStep] = useState(1)

  const [startId, setStartId] = useState<string>('')
  const [verified, setVerified] = useState(false)
  const [checkedIds, setCheckedIds] = useState(false)
  const [uploadResults, setUploadResults] = useState<UploadResult[]>([])

  const [pendingFiles, setPendingFiles] = useState<PendingItem[]>([])
  const [activeSlot, setActiveSlot] = useState<Slot | ''>('')
  const [uploading, setUploading] = useState(false)
  const [locked, setLocked] = useState<LockedItem[]>([])
  const [idMap, setIdMap] = useState<Map<number, number>>(new Map())
  const [lockError, setLockError] = useState<string | null>(null)

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    icon: '',
    effectImageId: '',
    frame: '6',
    gender: '3',
    level: '0',
    require: '0',
    resale_price: '-1',
    options: '[]',
  })
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [checkingImage, setCheckingImage] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [generatedCode, setGeneratedCode] = useState<string>('')
  const [itemId, setItemId] = useState<number | null>(null)
  const [codeAddedToFile, setCodeAddedToFile] = useState(false)
  const [addingCode, setAddingCode] = useState(false)

  useEffect(() => {
    const icon = uploadResults.find((r) => {
      const lockedItem = locked.find((l) => l.newId === r.imageId)
      return lockedItem?.slot === 'icon'
    })
    if (icon) {
      setFormData((prev) => ({ ...prev, icon: icon.imageId.toString() }))
    }

    const effect = uploadResults.find((r) => {
      const lockedItem = locked.find((l) => l.newId === r.imageId)
      return lockedItem?.slot === 'effect'
    })
    if (effect) {
      setFormData((prev) => ({ ...prev, effectImageId: effect.imageId.toString() }))
    }
  }, [uploadResults, locked])

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
      rows.push({ file, preview, oldId, slot: '' })
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
    const icon = pendingFiles.find((p) => p.slot === 'icon')
    const effect = pendingFiles.find((p) => p.slot === 'effect')
    if (!icon || !effect) {
      setPopup({ message: 'Chọn ảnh icon và effect trước', type: 'error' })
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
      const uniqueOldIds = new Set<number>()
      uniqueOldIds.add(icon.oldId)
      uniqueOldIds.add(effect.oldId)
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
        const sorted = [icon, effect].sort((a, b) => a.oldId - b.oldId)
        const map = new Map<number, number>()
        let nextIdx = 0
        const lockedRows: LockedItem[] = []
        sorted.forEach((row) => {
          const newId = startIdNum + nextIdx
          map.set(row.oldId, newId)
          lockedRows.push({ slot: row.slot as Slot, file: row.file, preview: row.preview, oldId: row.oldId, newId })
          nextIdx++
        })
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

  const uploadLockedAll = async () => {
    if (!locked.length) {
      setPopup({ message: 'Chưa lock ID', type: 'error' })
      return
    }

    setUploading(true)
    const results: UploadResult[] = []
    const errors: string[] = []

    const enqueue = async (file: File, newId: number) => {
      try {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('fileIndex', '0')
        formData.append('startId', newId.toString())
        const response = await fetch('/api/upload', { method: 'POST', body: formData })
        const data = await response.json()
        if (!response.ok) throw new Error(data.error || 'Upload thất bại')
        results.push({ fileName: file.name, imageId: newId, paths: data.paths || {} })
      } catch (err) {
        errors.push(`${file.name}: ${err instanceof Error ? err.message : 'Upload lỗi'}`)
      }
    }

    for (const row of locked) {
      await enqueue(row.file, row.newId)
    }

    setUploadResults((prev) => [...prev, ...results])
    setUploading(false)
    setCheckedIds(true)
    if (errors.length) {
      setPopup({ message: `${errors.length} lỗi: ${errors.slice(0, 3).join(', ')}${errors.length > 3 ? '...' : ''}`, type: 'error' })
    } else {
      setPopup({ message: `Upload xong ${results.length} ảnh`, type: 'success' })
      setStep(3)
    }
  }

  const handleChangeForm = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    if (name === 'options') {
      if (value.trim() === '') {
        setOptionsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setOptionsError('Options phải là mảng JSON')
          } else {
            setOptionsError(null)
          }
        } catch {
          setOptionsError('JSON không hợp lệ')
        }
      }
    }
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
      setPopup({ message: 'Điền đủ tên, mô tả và icon', type: 'error' })
      return
    }
    
    const effectLocked = locked.find((l) => l.slot === 'effect')
    if (!effectLocked) {
      setPopup({ message: 'Chưa upload ảnh effect', type: 'error' })
      return
    }
    
    const icon = parseInt(formData.icon)
    const effectImageId = effectLocked.newId
    const frame = parseInt(formData.frame)
    if (isNaN(icon) || icon < 0 || isNaN(frame) || frame < 0) {
      setPopup({ message: 'Icon và Frame phải là số nguyên dương', type: 'error' })
      return
    }
    if (optionsError) {
      setPopup({ message: 'Vui lòng sửa lỗi JSON options', type: 'error' })
      return
    }
    setSubmitting(true)
    try {
      const response = await fetch('/api/add-danh-hieu', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name,
          description: formData.description,
          icon,
          gender: parseInt(formData.gender) || 3,
          level: parseInt(formData.level) || 0,
          require: parseInt(formData.require) || 0,
          resale_price: parseInt(formData.resale_price) || -1,
          options: formData.options.trim() || '[]',
        }),
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Thêm danh hiệu thất bại')
      
      setItemId(data.id)
      const code = `        danhHieuMap.put(${data.id}, new int[]{${effectImageId}, ${frame}});`
      setGeneratedCode(code)
      
      setAddingCode(true)
      try {
        const codeResponse = await fetch('/api/add-danh-hieu-code', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            itemId: data.id,
            effectImageId,
            frame,
          }),
        })
        const codeData = await codeResponse.json()
        if (codeResponse.ok) {
          setCodeAddedToFile(true)
          setPopup({ message: `${data.message}. Đã tự động thêm code vào DanhHieu.java`, type: 'success' })
        } else {
          setPopup({ message: `${data.message}. Lỗi thêm code: ${codeData.error}`, type: 'error' })
        }
      } catch (codeErr) {
        setPopup({ message: `${data.message}. Lỗi thêm code: ${codeErr instanceof Error ? codeErr.message : 'Lỗi không xác định'}`, type: 'error' })
      } finally {
        setAddingCode(false)
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Thêm danh hiệu thất bại'
      setPopup({ message: msg, type: 'error' })
    } finally {
      setSubmitting(false)
    }
  }

  const handleCopyCode = async () => {
    if (!generatedCode) return
    try {
      await navigator.clipboard.writeText(generatedCode)
      setPopup({ message: 'Đã copy code Java', type: 'success' })
    } catch {
      setPopup({ message: 'Copy thất bại', type: 'error' })
    }
  }

  const handleAddToJavaFile = async () => {
    if (!itemId || !formData.frame) {
      setPopup({ message: 'Thiếu thông tin để thêm code', type: 'error' })
      return
    }
    const effectLocked = locked.find((l) => l.slot === 'effect')
    if (!effectLocked) {
      setPopup({ message: 'Chưa có ảnh effect', type: 'error' })
      return
    }
    setAddingCode(true)
    try {
      const response = await fetch('/api/add-danh-hieu-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          itemId,
          effectImageId: effectLocked.newId,
          frame: parseInt(formData.frame),
        }),
      })
      const data = await response.json()
      if (!response.ok) throw new Error(data.error || 'Thêm code thất bại')
      setCodeAddedToFile(true)
      setPopup({ message: 'Đã thêm code vào DanhHieu.java', type: 'success' })
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Thêm code thất bại'
      setPopup({ message: msg, type: 'error' })
    } finally {
      setAddingCode(false)
    }
  }

  const canGoStep3 = checkedIds && uploadResults.length > 0

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
              <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Danh Hiệu Mix</h1>
              <p className="text-sm text-gray-600">Upload ảnh icon + effect → thêm danh hiệu → gen code Java</p>
            </div>
            <div className="flex gap-2 text-sm">
              <span className={`px-3 py-1 rounded-full ${step === 1 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-700'}`}>1. Upload</span>
              <span className={`px-3 py-1 rounded-full ${step === 3 ? 'bg-purple-600 text-white' : 'bg-gray-200 text-gray-700'}`}>2. Item</span>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-lg">
                <UploadIcon className="w-6 h-6 text-blue-600" />
              </div>
              <div className="flex-1">
                <h2 className="text-xl md:text-2xl font-bold text-gray-900">Bước 1: Chọn ảnh + Lock ID</h2>
                <p className="text-sm text-gray-500">Icon (cho nr_item) + Effect (cho DanhHieu.java)</p>
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
              </div>
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <p className="text-sm font-medium text-gray-700 mb-2">Mapping đã lock</p>
                {lockError && <p className="text-sm text-red-600">{lockError}</p>}
                {!locked.length ? (
                  <p className="text-xs text-gray-500">Chưa lock. Chọn ảnh icon và effect rồi bấm "Kiểm tra".</p>
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
                    <option value="icon">Icon</option>
                    <option value="effect">Effect</option>
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
                            for (let i = 0; i < next.length; i++) {
                              if (i !== idx && next[i].slot === 'icon') {
                                next[i] = { ...next[i], slot: '' }
                              }
                            }
                          }
                          if (activeSlot === 'effect') {
                            for (let i = 0; i < next.length; i++) {
                              if (i !== idx && next[i].slot === 'effect') {
                                next[i] = { ...next[i], slot: '' }
                              }
                            }
                          }

                          const nextSlot = cur.slot === activeSlot ? '' : activeSlot
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
                        <div className="text-[10px] text-gray-500">
                          <span>Slot: {p.slot ? slotMeta[p.slot].label : 'Chưa chọn'}</span>
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

          <div className={`bg-white rounded-xl shadow-lg p-6 md:p-8 ${!canGoStep3 ? 'opacity-60 pointer-events-none' : ''}`}>
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-green-100 rounded-lg">
                <SaveIcon className="w-6 h-6 text-green-600" />
              </div>
              <div className="flex-1">
                <h2 className="text-xl md:text-2xl font-bold text-gray-900">Bước 2: Thêm danh hiệu + Gen code Java</h2>
                <p className="text-sm text-gray-500">Điền thông tin item và tự động gen code để thêm vào DanhHieu.java</p>
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
                    Effect Image ID
                  </label>
                  <input
                    type="text"
                    value={locked.find((l) => l.slot === 'effect')?.newId || 'Chưa upload'}
                    disabled
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 cursor-not-allowed"
                    placeholder="Tự động lấy từ ảnh effect đã upload"
                  />
                  <p className="text-xs text-gray-500 mt-1">Tự động lấy từ ảnh effect đã upload ở bước 1</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Frame <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="frame"
                    value={formData.frame}
                    onChange={handleChangeForm}
                    required
                    min="1"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Mặc định: 6"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tên Danh Hiệu <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChangeForm}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Ví dụ: Danh hiệu thần thoại"
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

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Level</label>
                  <input
                    type="number"
                    name="level"
                    value={formData.level}
                    onChange={handleChangeForm}
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Require</label>
                  <input
                    type="number"
                    name="require"
                    value={formData.require}
                    onChange={handleChangeForm}
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Resale Price</label>
                  <input
                    type="number"
                    name="resale_price"
                    value={formData.resale_price}
                    onChange={handleChangeForm}
                    min="-1"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="-1"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Options (JSON)
                </label>
                <textarea
                  name="options"
                  value={formData.options}
                  onChange={handleChangeForm}
                  rows={3}
                  className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${
                    optionsError ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder='[{"id":88,"param":5}]'
                />
                {optionsError && (
                  <p className="text-xs text-red-500 mt-1">{optionsError}</p>
                )}
                <p className="text-xs text-gray-500 mt-1">
                  JSON array cho item options, để trống nếu không có
                </p>
              </div>

              {generatedCode && (
                <div className={`border rounded-lg p-4 ${codeAddedToFile ? 'bg-green-50 border-green-200' : 'bg-yellow-50 border-yellow-200'}`}>
                  <div className="flex items-center justify-between mb-2">
                    <label className="block text-sm font-medium text-gray-700">
                      {codeAddedToFile ? '✓ Đã thêm vào DanhHieu.java' : 'Code Java đã gen'}
                    </label>
                    <div className="flex gap-2">
                      {!codeAddedToFile && (
                        <Button 
                          type="button" 
                          onClick={handleAddToJavaFile} 
                          disabled={addingCode}
                          variant="outline" 
                          size="sm"
                        >
                          {addingCode ? (
                            <>
                              <LoaderIcon className="w-4 h-4 mr-2 animate-spin" />
                              Đang thêm...
                            </>
                          ) : (
                            <>
                              <CodeIcon className="w-4 h-4 mr-2" />
                              Thêm vào file
                            </>
                          )}
                        </Button>
                      )}
                      <Button type="button" onClick={handleCopyCode} variant="outline" size="sm">
                        <CodeIcon className="w-4 h-4 mr-2" />
                        Copy
                      </Button>
                    </div>
                  </div>
                  <pre className="bg-white border border-gray-300 rounded-lg p-3 text-xs font-mono overflow-x-auto">
                    {generatedCode}
                  </pre>
                  <p className="text-xs text-gray-600 mt-2">
                    Item ID: {itemId} | Effect Image ID: {locked.find((l) => l.slot === 'effect')?.newId || 'N/A'} | Frame: {formData.frame}
                  </p>
                </div>
              )}

              <div className="flex items-end justify-end gap-2">
                <Button type="submit" disabled={submitting} className="min-w-[160px]">
                  {submitting ? (
                    <>
                      <LoaderIcon className="w-5 h-5 mr-2 animate-spin inline" />
                      Đang lưu...
                    </>
                  ) : (
                    <>
                      <SaveIcon className="w-5 h-5 mr-2 inline" />
                      Lưu danh hiệu
                    </>
                  )}
                </Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  )
}

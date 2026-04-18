'use client'

import React, { useState, useEffect } from 'react'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  LoaderIcon,
  CheckCircleIcon,
  AlertCircleIcon,
  PlusIcon,
  TrashIcon,
  EditIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

interface MocDiem {
  id: number
  diem_can_thiet: number
  item_id_1: string | null
  item_id_2: string | null
  item_id_3: string | null
  item_id_4: string | null
  active: number
  sort_order: number
  created_at: string
  updated_at: string
}

interface ItemOption {
  id: number
  name: string
  type: number
}

interface NoHuItem {
  id: number
  quantity: number
  expire?: number
  options?: Array<{ id: number; param: number }>
}

interface NoHuGroup {
  ratioNoHu: number
  items: NoHuItem[]
}

export default function BoMongNoHuPage() {
  const [mocDiems, setMocDiems] = useState<MocDiem[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedMocDiem, setSelectedMocDiem] = useState<MocDiem | null>(null)
  const [showAddModal, setShowAddModal] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [itemOptions, setItemOptions] = useState<ItemOption[]>([])
  const [loadingOptions, setLoadingOptions] = useState(false)

  const loadMocDiems = async () => {
    setLoading(true)
    try {
      const response = await fetch('/api/bo-mong-moc-diem')
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách mốc điểm thất bại')
      }

      setMocDiems(data.data || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Lấy danh sách mốc điểm thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setLoading(false)
    }
  }

  const loadItemOptions = async () => {
    setLoadingOptions(true)
    try {
      const response = await fetch('/api/item-options')
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách options thất bại')
      }

      setItemOptions(data.options || [])
    } catch (err) {
      console.error('Load item options error:', err)
    } finally {
      setLoadingOptions(false)
    }
  }

  useEffect(() => {
    loadMocDiems()
    loadItemOptions()
  }, [])

  const handleDelete = async (id: number) => {
    if (!confirm('Bạn có chắc muốn xóa mốc điểm này?')) {
      return
    }

    try {
      const response = await fetch(`/api/bo-mong-moc-diem/${id}`, {
        method: 'DELETE',
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Xóa mốc điểm thất bại')
      }

      setPopup({ message: data.message, type: 'success' })
      loadMocDiems()
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Xóa mốc điểm thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    }
  }

  const parseGroupJson = (jsonStr: string | null): NoHuGroup | null => {
    if (!jsonStr || jsonStr.trim() === '') return null
    try {
      return JSON.parse(jsonStr)
    } catch {
      return null
    }
  }

  const getOptionName = (optionId: number): string => {
    const option = itemOptions.find(opt => opt.id === optionId)
    return option ? option.name : `Option ${optionId}`
  }

  return (
    <>
      {popup && (
        <Popup
          message={popup.message}
          type={popup.type}
          onClose={() => setPopup(null)}
        />
      )}
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-3">
                <div className="p-2 bg-purple-100 rounded-lg">
                  <ImageIcon className="w-6 h-6 text-purple-600" />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-900">Quản lý Nổ Hũ Bò Mộng</h1>
                  <p className="text-sm text-gray-600">Tổng số: {mocDiems.length} mốc điểm</p>
                </div>
              </div>
              <Button
                onClick={() => setShowAddModal(true)}
              >
                <PlusIcon className="w-4 h-4 mr-2" />
                Thêm Mốc Điểm
              </Button>
            </div>

            {loading ? (
              <div className="text-center py-12">
                <LoaderIcon className="w-12 h-12 mx-auto animate-spin text-purple-600" />
                <p className="text-gray-600 mt-4">Đang tải...</p>
              </div>
            ) : mocDiems.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">Chưa có mốc điểm nào</p>
              </div>
            ) : (
              <div className="space-y-4">
                {mocDiems.map((moc) => {
                  const groups = [
                    parseGroupJson(moc.item_id_1),
                    parseGroupJson(moc.item_id_2),
                    parseGroupJson(moc.item_id_3),
                    parseGroupJson(moc.item_id_4),
                  ].filter(g => g !== null) as NoHuGroup[]

                  return (
                    <div
                      key={moc.id}
                      className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow bg-white"
                    >
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2">
                            <h3 className="text-lg font-bold text-gray-900">
                              Mốc {moc.diem_can_thiet} điểm
                            </h3>
                            {moc.active === 1 ? (
                              <span className="px-2 py-1 text-xs font-medium rounded bg-green-100 text-green-700">
                                Hoạt động
                              </span>
                            ) : (
                              <span className="px-2 py-1 text-xs font-medium rounded bg-gray-100 text-gray-700">
                                Tắt
                              </span>
                            )}
                            <span className="text-xs text-gray-500">
                              Sort: {moc.sort_order}
                            </span>
                          </div>
                          <p className="text-xs text-gray-500">
                            ID: {moc.id} | Tạo: {new Date(moc.created_at).toLocaleString('vi-VN')}
                          </p>
                        </div>
                        <div className="flex gap-2">
                          <Button
                            onClick={() => setSelectedMocDiem(moc)}
                            variant="outline"
                            size="sm"
                          >
                            <EditIcon className="w-4 h-4 mr-1" />
                            Sửa
                          </Button>
                          <Button
                            onClick={() => handleDelete(moc.id)}
                            variant="outline"
                            size="sm"
                            className="text-red-600 hover:text-red-700 hover:border-red-300"
                          >
                            <TrashIcon className="w-4 h-4" />
                          </Button>
                        </div>
                      </div>

                      {groups.length > 0 && (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-4">
                          {groups.map((group, idx) => (
                            <div
                              key={idx}
                              className="border border-purple-200 rounded-lg p-3 bg-purple-50"
                            >
                              <div className="mb-2">
                                <span className="text-xs font-medium text-purple-700">
                                  Nhóm {idx + 1}: {group.ratioNoHu}%
                                </span>
                              </div>
                              <div className="space-y-2">
                                {group.items.map((item, itemIdx) => {
                                  const itemIdNum = typeof item.id === 'number' ? item.id : parseInt(String(item.id)) || 0
                                  return (
                                    <div
                                      key={itemIdx}
                                      className="bg-white rounded p-2 text-xs border border-purple-100"
                                    >
                                      <div className="flex items-center gap-2 mb-1">
                                        <ItemIcon itemId={itemIdNum} />
                                        <span className="font-medium">ID: {item.id}</span>
                                        <span className="text-gray-600">x{item.quantity}</span>
                                      </div>
                                      {item.expire && (
                                        <div className="text-gray-500 text-xs">
                                          Hết hạn: {item.expire}s
                                        </div>
                                      )}
                                      {item.options && item.options.length > 0 && (
                                        <div className="mt-1 space-y-0.5">
                                          {item.options.map((opt, optIdx) => (
                                            <div key={optIdx} className="text-gray-600 text-xs">
                                              {getOptionName(opt.id)}: {opt.param}
                                            </div>
                                          ))}
                                        </div>
                                      )}
                                    </div>
                                  )
                                })}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </div>
      </div>

      {showAddModal && (
        <AddMocDiemModal
          itemOptions={itemOptions}
          onClose={() => {
            setShowAddModal(false)
            loadMocDiems()
          }}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setShowAddModal(false)
            loadMocDiems()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}

      {selectedMocDiem && (
        <EditMocDiemModal
          mocDiem={selectedMocDiem}
          itemOptions={itemOptions}
          onClose={() => {
            setSelectedMocDiem(null)
            loadMocDiems()
          }}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setSelectedMocDiem(null)
            loadMocDiems()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
    </>
  )
}

interface ItemIconProps {
  itemId: number
}

function ItemIcon({ itemId }: ItemIconProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(false)
  const [iconId, setIconId] = useState<number | null>(null)

  useEffect(() => {
    setImageUrl(null)
    setError(false)
    setIconId(null)
    
    if (!itemId || itemId <= 0 || isNaN(itemId)) {
      return
    }

    setLoading(true)
    
    fetch(`/api/item-icon?itemId=${itemId}`)
      .then(res => res.json())
      .then(data => {
        if (data.success && data.iconId) {
          setIconId(data.iconId)
          return fetch('/api/check-image', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ imageId: data.iconId }),
          })
        } else {
          setError(true)
          setLoading(false)
          return null
        }
      })
      .then(res => {
        if (!res) return
        if (!res.ok) {
          throw new Error('API error')
        }
        return res.json()
      })
      .then(data => {
        if (data && data.success && data.exists && data.imagePath) {
          setImageUrl(data.imagePath)
          setError(false)
        } else {
          setError(true)
        }
      })
      .catch(() => {
        setError(true)
      })
      .finally(() => setLoading(false))
  }, [itemId])

  if (!itemId || itemId <= 0 || isNaN(itemId)) {
    return (
      <div className="w-8 h-8 bg-gray-100 rounded border border-gray-300 flex items-center justify-center flex-shrink-0">
        <span className="text-[8px] text-gray-400">-</span>
      </div>
    )
  }

  return (
    <div className="w-8 h-8 bg-white rounded border border-gray-300 flex items-center justify-center flex-shrink-0 overflow-hidden">
      {loading ? (
        <LoaderIcon className="w-4 h-4 animate-spin text-gray-400" />
      ) : imageUrl ? (
        <img
          src={imageUrl}
          alt={`Item ${itemId}`}
          className="w-full h-full object-contain rounded"
          onError={() => {
            setImageUrl(null)
            setError(true)
          }}
        />
      ) : error ? (
        <span className="text-[8px] text-gray-400" title={`Item ID: ${itemId}`}>
          {itemId}
        </span>
      ) : (
        <span className="text-[8px] text-gray-400">ID</span>
      )}
    </div>
  )
}

interface AddMocDiemModalProps {
  itemOptions: ItemOption[]
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

function AddMocDiemModal({ itemOptions, onClose, onSuccess, onError }: AddMocDiemModalProps) {
  const [formData, setFormData] = useState({
    diem_can_thiet: '',
    sort_order: '0',
    active: '1',
    groups: [
      { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] },
      { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] },
      { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] },
      { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] },
    ] as Array<{ ratioNoHu: string; items: Array<{ id: string; quantity: string; expire: string; options: Array<{ id: string; param: string }> }> }>,
  })
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.diem_can_thiet || parseInt(formData.diem_can_thiet) < 0) {
      onError('Điểm cần thiết không hợp lệ')
      return
    }

    const groupsJson: (string | null)[] = []
    for (let i = 0; i < 4; i++) {
      const group = formData.groups[i]
      if (group.ratioNoHu && group.items.some(item => item.id && item.quantity)) {
        const validItems = group.items
          .filter(item => item.id && item.quantity)
          .map(item => {
            const itemObj: any = {
              id: parseInt(item.id),
              quantity: parseInt(item.quantity) || 1,
            }
            if (item.expire) {
              itemObj.expire = parseInt(item.expire)
            }
            if (item.options && item.options.length > 0) {
              itemObj.options = item.options
                .filter(opt => opt.id && opt.param)
                .map(opt => ({
                  id: parseInt(opt.id),
                  param: parseInt(opt.param) || 0,
                }))
            }
            return itemObj
          })

        if (validItems.length > 0) {
          groupsJson[i] = JSON.stringify({
            ratioNoHu: parseInt(group.ratioNoHu),
            items: validItems,
          })
        } else {
          groupsJson[i] = null
        }
      } else {
        groupsJson[i] = null
      }
    }

    setSubmitting(true)
    try {
      const response = await fetch('/api/bo-mong-moc-diem', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          diem_can_thiet: parseInt(formData.diem_can_thiet),
          item_id_1: groupsJson[0],
          item_id_2: groupsJson[1],
          item_id_3: groupsJson[2],
          item_id_4: groupsJson[3],
          active: parseInt(formData.active),
          sort_order: parseInt(formData.sort_order),
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Tạo mốc điểm thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Tạo mốc điểm thất bại'
      onError(errorMsg)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <MocDiemModal
      title="Thêm Mốc Điểm Mới"
      formData={formData}
      setFormData={setFormData}
      itemOptions={itemOptions}
      submitting={submitting}
      onSubmit={handleSubmit}
      onClose={onClose}
    />
  )
}

interface EditMocDiemModalProps {
  mocDiem: MocDiem
  itemOptions: ItemOption[]
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

function EditMocDiemModal({ mocDiem, itemOptions, onClose, onSuccess, onError }: EditMocDiemModalProps) {
  const parseGroupToFormData = (jsonStr: string | null): { ratioNoHu: string; items: Array<{ id: string; quantity: string; expire: string; options: Array<{ id: string; param: string }> }> } => {
    if (!jsonStr) {
      return { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] }
    }
    try {
      const group: NoHuGroup = JSON.parse(jsonStr)
      return {
        ratioNoHu: group.ratioNoHu.toString(),
        items: group.items.map(item => ({
          id: item.id.toString(),
          quantity: item.quantity.toString(),
          expire: item.expire?.toString() || '',
          options: item.options?.map(opt => ({ id: opt.id.toString(), param: opt.param.toString() })) || [],
        })),
      }
    } catch {
      return { ratioNoHu: '', items: [{ id: '', quantity: '', expire: '', options: [] }] }
    }
  }

  const [formData, setFormData] = useState({
    diem_can_thiet: mocDiem.diem_can_thiet.toString(),
    sort_order: mocDiem.sort_order.toString(),
    active: mocDiem.active.toString(),
    groups: [
      parseGroupToFormData(mocDiem.item_id_1),
      parseGroupToFormData(mocDiem.item_id_2),
      parseGroupToFormData(mocDiem.item_id_3),
      parseGroupToFormData(mocDiem.item_id_4),
    ],
  })
  const [updating, setUpdating] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const groupsJson: (string | null)[] = []
    for (let i = 0; i < 4; i++) {
      const group = formData.groups[i]
      if (group.ratioNoHu && group.items.some(item => item.id && item.quantity)) {
        const validItems = group.items
          .filter(item => item.id && item.quantity)
          .map(item => {
            const itemObj: any = {
              id: parseInt(item.id),
              quantity: parseInt(item.quantity) || 1,
            }
            if (item.expire) {
              itemObj.expire = parseInt(item.expire)
            }
            if (item.options && item.options.length > 0) {
              itemObj.options = item.options
                .filter(opt => opt.id && opt.param)
                .map(opt => ({
                  id: parseInt(opt.id),
                  param: parseInt(opt.param) || 0,
                }))
            }
            return itemObj
          })

        if (validItems.length > 0) {
          groupsJson[i] = JSON.stringify({
            ratioNoHu: parseInt(group.ratioNoHu),
            items: validItems,
          })
        } else {
          groupsJson[i] = null
        }
      } else {
        groupsJson[i] = null
      }
    }

    setUpdating(true)
    try {
      const response = await fetch(`/api/bo-mong-moc-diem/${mocDiem.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          diem_can_thiet: parseInt(formData.diem_can_thiet),
          item_id_1: groupsJson[0],
          item_id_2: groupsJson[1],
          item_id_3: groupsJson[2],
          item_id_4: groupsJson[3],
          active: parseInt(formData.active),
          sort_order: parseInt(formData.sort_order),
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Cập nhật mốc điểm thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Cập nhật mốc điểm thất bại'
      onError(errorMsg)
    } finally {
      setUpdating(false)
    }
  }

  return (
    <MocDiemModal
      title="Chỉnh sửa Mốc Điểm"
      formData={formData}
      setFormData={setFormData}
      itemOptions={itemOptions}
      submitting={updating}
      onSubmit={handleSubmit}
      onClose={onClose}
    />
  )
}

interface MocDiemModalProps {
  title: string
  formData: {
    diem_can_thiet: string
    sort_order: string
    active: string
    groups: Array<{ ratioNoHu: string; items: Array<{ id: string; quantity: string; expire: string; options: Array<{ id: string; param: string }> }> }>
  }
  setFormData: (data: any) => void
  itemOptions: ItemOption[]
  submitting: boolean
  onSubmit: (e: React.FormEvent) => void
  onClose: () => void
}

function MocDiemModal({ title, formData, setFormData, itemOptions, submitting, onSubmit, onClose }: MocDiemModalProps) {
  const addItemToGroup = (groupIndex: number) => {
    const newGroups = [...formData.groups]
    newGroups[groupIndex].items.push({ id: '', quantity: '', expire: '', options: [] })
    setFormData({ ...formData, groups: newGroups })
  }

  const removeItemFromGroup = (groupIndex: number, itemIndex: number) => {
    const newGroups = [...formData.groups]
    newGroups[groupIndex].items.splice(itemIndex, 1)
    if (newGroups[groupIndex].items.length === 0) {
      newGroups[groupIndex].items.push({ id: '', quantity: '', expire: '', options: [] })
    }
    setFormData({ ...formData, groups: newGroups })
  }

  const addOptionToItem = (groupIndex: number, itemIndex: number) => {
    const newGroups = [...formData.groups]
    if (!newGroups[groupIndex].items[itemIndex].options) {
      newGroups[groupIndex].items[itemIndex].options = []
    }
    newGroups[groupIndex].items[itemIndex].options!.push({ id: '', param: '' })
    setFormData({ ...formData, groups: newGroups })
  }

  const removeOptionFromItem = (groupIndex: number, itemIndex: number, optionIndex: number) => {
    const newGroups = [...formData.groups]
    if (newGroups[groupIndex].items[itemIndex].options) {
      newGroups[groupIndex].items[itemIndex].options!.splice(optionIndex, 1)
    }
    setFormData({ ...formData, groups: newGroups })
  }

  const updateGroupField = (groupIndex: number, field: string, value: string) => {
    const newGroups = [...formData.groups]
    ;(newGroups[groupIndex] as any)[field] = value
    setFormData({ ...formData, groups: newGroups })
  }

  const updateItemField = (groupIndex: number, itemIndex: number, field: string, value: string) => {
    const newGroups = [...formData.groups]
    ;(newGroups[groupIndex].items[itemIndex] as any)[field] = value
    setFormData({ ...formData, groups: newGroups })
  }

  const updateOptionField = (groupIndex: number, itemIndex: number, optionIndex: number, field: string, value: string) => {
    const newGroups = [...formData.groups]
    if (!newGroups[groupIndex].items[itemIndex].options) {
      newGroups[groupIndex].items[itemIndex].options = []
    }
    ;(newGroups[groupIndex].items[itemIndex].options![optionIndex] as any)[field] = value
    setFormData({ ...formData, groups: newGroups })
  }

  const getOptionName = (optionId: string): string => {
    if (!optionId) return 'Chọn option'
    const id = parseInt(optionId)
    const option = itemOptions.find(opt => opt.id === id)
    return option ? option.name : `Option ${id}`
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm overflow-y-auto">
      <div className="bg-white rounded-xl shadow-2xl max-w-6xl w-full max-h-[90vh] overflow-y-auto my-8">
        <div className="p-6 sticky top-0 bg-white border-b z-10">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">{title}</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <form onSubmit={onSubmit} className="space-y-4">
            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Điểm Cần Thiết <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  value={formData.diem_can_thiet}
                  onChange={(e) => setFormData({ ...formData, diem_can_thiet: e.target.value })}
                  required
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Sort Order
                </label>
                <input
                  type="number"
                  value={formData.sort_order}
                  onChange={(e) => setFormData({ ...formData, sort_order: e.target.value })}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Trạng Thái
                </label>
                <select
                  value={formData.active}
                  onChange={(e) => setFormData({ ...formData, active: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                >
                  <option value="1">Hoạt động</option>
                  <option value="0">Tắt</option>
                </select>
              </div>
            </div>

            <div className="space-y-6 mt-6">
              <h3 className="text-lg font-semibold text-gray-900">Các Nhóm Quà (Tối đa 4 nhóm)</h3>
              {formData.groups.map((group, groupIndex) => (
                <div key={groupIndex} className="border border-purple-200 rounded-lg p-4 bg-purple-50">
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nhóm {groupIndex + 1} - Tỉ Lệ Nổ Hũ (%)
                    </label>
                    <input
                      type="number"
                      value={group.ratioNoHu}
                      onChange={(e) => updateGroupField(groupIndex, 'ratioNoHu', e.target.value)}
                      min="0"
                      max="100"
                      placeholder="Ví dụ: 20"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                    />
                  </div>

                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <label className="block text-sm font-medium text-gray-700">
                        Items trong nhóm này
                      </label>
                      <Button
                        type="button"
                        onClick={() => addItemToGroup(groupIndex)}
                        variant="outline"
                        size="sm"
                      >
                        <PlusIcon className="w-4 h-4 mr-1" />
                        Thêm Item
                      </Button>
                    </div>

                    {group.items.map((item, itemIndex) => (
                      <div key={itemIndex} className="bg-white rounded-lg p-4 border border-purple-100">
                        <div className="grid grid-cols-4 gap-3 mb-3">
                          <div>
                            <label className="block text-xs font-medium text-gray-700 mb-1">
                              Item ID <span className="text-red-500">*</span>
                            </label>
                            <div className="flex items-center gap-2">
                              <input
                                type="number"
                                value={item.id}
                                onChange={(e) => updateItemField(groupIndex, itemIndex, 'id', e.target.value)}
                                required
                                min="1"
                                className="flex-1 px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                                placeholder="ID"
                              />
                              {item.id && item.id.trim() !== '' && (
                                <ItemIcon itemId={parseInt(item.id) || 0} />
                              )}
                            </div>
                          </div>
                          <div>
                            <label className="block text-xs font-medium text-gray-700 mb-1">
                              Số Lượng <span className="text-red-500">*</span>
                            </label>
                            <input
                              type="number"
                              value={item.quantity}
                              onChange={(e) => updateItemField(groupIndex, itemIndex, 'quantity', e.target.value)}
                              required
                              min="1"
                              className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                              placeholder="1"
                            />
                          </div>
                          <div>
                            <label className="block text-xs font-medium text-gray-700 mb-1">
                              Hết Hạn (giây)
                            </label>
                            <input
                              type="number"
                              value={item.expire}
                              onChange={(e) => updateItemField(groupIndex, itemIndex, 'expire', e.target.value)}
                              min="0"
                              className="w-full px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                              placeholder="0 = vĩnh viễn"
                            />
                          </div>
                          <div className="flex items-end">
                            {group.items.length > 1 && (
                              <Button
                                type="button"
                                onClick={() => removeItemFromGroup(groupIndex, itemIndex)}
                                variant="outline"
                                size="sm"
                                className="text-red-600 hover:text-red-700 hover:border-red-300"
                              >
                                <TrashIcon className="w-4 h-4" />
                              </Button>
                            )}
                          </div>
                        </div>

                        <div className="space-y-2">
                          <div className="flex items-center justify-between">
                            <label className="block text-xs font-medium text-gray-700">
                              Options
                            </label>
                            <Button
                              type="button"
                              onClick={() => addOptionToItem(groupIndex, itemIndex)}
                              variant="outline"
                              size="sm"
                            >
                              <PlusIcon className="w-3 h-3 mr-1" />
                              Thêm Option
                            </Button>
                          </div>

                          {item.options && item.options.length > 0 && (
                            <div className="space-y-2">
                              {item.options.map((option, optionIndex) => (
                                <div key={optionIndex} className="flex items-center gap-2">
                                  <select
                                    value={option.id}
                                    onChange={(e) => updateOptionField(groupIndex, itemIndex, optionIndex, 'id', e.target.value)}
                                    className="flex-1 px-3 py-1.5 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                                  >
                                    <option value="">Chọn option</option>
                                    {itemOptions.map((opt) => (
                                      <option key={opt.id} value={opt.id.toString()}>
                                        {opt.name} (ID: {opt.id})
                                      </option>
                                    ))}
                                  </select>
                                  <input
                                    type="number"
                                    value={option.param}
                                    onChange={(e) => updateOptionField(groupIndex, itemIndex, optionIndex, 'param', e.target.value)}
                                    placeholder="Param"
                                    className="w-24 px-2 py-1.5 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                                  />
                                  <Button
                                    type="button"
                                    onClick={() => removeOptionFromItem(groupIndex, itemIndex, optionIndex)}
                                    variant="outline"
                                    size="sm"
                                    className="text-red-600 hover:text-red-700"
                                  >
                                    <TrashIcon className="w-3 h-3" />
                                  </Button>
                                </div>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" onClick={onClose} variant="outline">
                Hủy
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <>
                    <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                    Đang lưu...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-4 h-4 mr-2 inline" />
                    Lưu
                  </>
                )}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

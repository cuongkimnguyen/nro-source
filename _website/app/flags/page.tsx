'use client'

import { useState, useEffect } from 'react'
import { useSearchParams, useRouter } from 'next/navigation'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  LoaderIcon,
  SearchIcon,
  CheckCircleIcon,
  AlertCircleIcon,
  PlusIcon,
  EditIcon,
  TrashIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'
import FlagbagPreview from '@/components/ui/FlagbagPreview'

const GENDERS: Record<number, string> = {
  0: 'Trái Đất',
  1: 'Namec',
  2: 'Xayda',
  3: 'Tất cả',
}

interface Flag {
  id: number
  name: string
  gender: number
  description: string
  level: number
  require: number
  resale_price: number
  icon: number
  part: number
  options: string
  is_up_to_up: number
}

interface FlagCardProps {
  flag: Flag
  onClick: () => void
  onCheck: () => void
  onDelete: () => void
  onTestFlagbag: () => void
}

function FlagCard({ flag, onClick, onCheck, onDelete, onTestFlagbag }: FlagCardProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (flag.icon && flag.icon > 0) {
      setLoading(true)
      fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: flag.icon }),
      })
        .then(res => res.json())
        .then(data => {
          if (data.exists && data.imagePath) {
            setImageUrl(data.imagePath)
          }
        })
        .catch(() => {})
        .finally(() => setLoading(false))
    }
  }, [flag.icon])

  return (
    <div className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow bg-white">
      <div className="flex gap-3">
        <div className="w-20 h-20 bg-gray-100 rounded border-2 border-gray-300 flex items-center justify-center flex-shrink-0">
          {loading ? (
            <LoaderIcon className="w-6 h-6 animate-spin text-gray-400" />
          ) : imageUrl ? (
            <img
              src={imageUrl}
              alt={flag.name}
              className="w-full h-full object-contain rounded"
              onError={() => setImageUrl(null)}
            />
          ) : (
            <span className="text-xs text-gray-400">No img</span>
          )}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2 mb-2">
            <div className="flex-1 min-w-0">
              <h3 className="text-base font-semibold text-gray-900 truncate" title={flag.name}>
                {flag.name}
              </h3>
              <p className="text-xs text-gray-500 mt-1">ID: {flag.id}</p>
            </div>
          </div>
          <p className="text-xs text-gray-600 mb-2 line-clamp-2">{flag.description || 'Không có mô tả'}</p>
          <div className="flex flex-wrap gap-2 mb-2">
            <span className="px-2 py-0.5 text-xs bg-green-100 text-green-700 rounded">
              {GENDERS[flag.gender] || 'Unknown'}
            </span>
            {flag.level > 0 && (
              <span className="px-2 py-0.5 text-xs bg-purple-100 text-purple-700 rounded">
                Lv.{flag.level}
              </span>
            )}
          </div>
          <div className="flex gap-2 mt-2 flex-wrap">
            <Button
              variant="outline"
              size="sm"
              onClick={(e) => {
                e.stopPropagation()
                onCheck()
              }}
            >
              <ImageIcon className="w-4 h-4 mr-1" />
              Test Icon
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={(e) => {
                e.stopPropagation()
                onTestFlagbag()
              }}
              className="bg-purple-50 hover:bg-purple-100 text-purple-700 border-purple-300"
            >
              <ImageIcon className="w-4 h-4 mr-1" />
              Test Flagbag
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={(e) => {
                e.stopPropagation()
                onClick()
              }}
            >
              <EditIcon className="w-4 h-4 mr-1" />
              Sửa
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={(e) => {
                e.stopPropagation()
                onDelete()
              }}
              className="text-red-600 hover:text-red-700 hover:border-red-300"
            >
              <TrashIcon className="w-4 h-4 mr-1" />
              Xóa
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}

interface AddFlagModalProps {
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

const AddFlagModal = ({ onClose, onSuccess, onError }: AddFlagModalProps) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    icon: '',
    gender: '3',
    level: '0',
    require: '0',
    resale_price: '-1',
    options: '[]',
    is_up_to_up: '0',
    bag_id: '',
    bag_name: '',
    bag_images: '[]',
  })
  const [submitting, setSubmitting] = useState(false)
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [bagImagesError, setBagImagesError] = useState<string | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'options') {
      if (value.trim() === '') {
        setOptionsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setOptionsError('Options phải là một mảng JSON')
          } else {
            setOptionsError(null)
          }
        } catch (err) {
          setOptionsError('JSON không hợp lệ')
        }
      }
    }
  }

  const handleCheckImage = async () => {
    const imageId = parseInt(formData.icon)
    if (!imageId || isNaN(imageId) || imageId < 1) {
      onError('ID ảnh không hợp lệ')
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
      if (data.exists && data.imagePath) {
        setImagePreview(data.imagePath)
      } else {
        setImagePreview(null)
        onError('Không tìm thấy ảnh với ID này')
      }
    } catch (err) {
      onError('Lỗi khi kiểm tra ảnh')
    } finally {
      setCheckingImage(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (optionsError) {
      onError('Vui lòng sửa lỗi JSON options')
      return
    }

    if (!formData.bag_id || parseInt(formData.bag_id) < 0) {
      onError('Vui lòng nhập Bag ID hợp lệ (>= 0)')
      return
    }
    if (!formData.bag_name.trim()) {
      onError('Vui lòng nhập tên Bag')
      return
    }
    if (bagImagesError) {
      onError('Vui lòng sửa lỗi JSON bag images')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch('/api/flags', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          icon: parseInt(formData.icon) || 0,
          gender: parseInt(formData.gender) || 3,
          level: parseInt(formData.level) || 0,
          require: parseInt(formData.require) || 0,
          resale_price: parseInt(formData.resale_price) || -1,
          is_up_to_up: parseInt(formData.is_up_to_up) || 0,
          bag_id: parseInt(formData.bag_id) || 0,
          bag_name: formData.bag_name,
          bag_images: formData.bag_images,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Thêm flag thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Thêm flag thất bại'
      onError(errorMsg)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Thêm Flag Mới</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tên Flag <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="VD: Cờ xanh"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mô tả
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={2}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Mô tả flag"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Icon ID <span className="text-red-500">*</span>
              </label>
              <div className="flex gap-2">
                <input
                  type="number"
                  name="icon"
                  value={formData.icon}
                  onChange={handleChange}
                  required
                  min="1"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="VD: 2330"
                />
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCheckImage}
                  disabled={checkingImage || !formData.icon}
                >
                  {checkingImage ? (
                    <LoaderIcon className="w-4 h-4 animate-spin" />
                  ) : (
                    'Kiểm tra'
                  )}
                </Button>
              </div>
              {imagePreview && (
                <div className="mt-2 w-20 h-20 border-2 border-gray-300 rounded bg-gray-100 flex items-center justify-center">
                  <img
                    src={imagePreview}
                    alt="Preview"
                    className="w-full h-full object-contain rounded"
                  />
                </div>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Gender
                </label>
                <select
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="0">Trái Đất</option>
                  <option value="1">Namec</option>
                  <option value="2">Xayda</option>
                  <option value="3">Tất cả</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Level
                </label>
                <input
                  type="number"
                  name="level"
                  value={formData.level}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Thông tin Bag (nr_clan_image)</h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bag ID <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="bag_id"
                    value={formData.bag_id}
                    onChange={handleChange}
                    min="0"
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="VD: 116"
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    ID này sẽ tự động dùng cho nr_item.part
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tên Bag <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="bag_name"
                    value={formData.bag_name}
                    onChange={handleChange}
                    required
                    maxLength={30}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="VD: Đeo lưng cờ Việt Nam"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Images (JSON Array) <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    name="bag_images"
                    value={formData.bag_images}
                    onChange={handleChange}
                    required
                    rows={3}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                      bagImagesError ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder='[9460,9461,9462,9463,9464]'
                  />
                  {bagImagesError && (
                    <p className="text-xs text-red-500 mt-1">{bagImagesError}</p>
                  )}
                  <p className="text-xs text-gray-500 mt-1">
                    Danh sách Image ID để animation, ví dụ: [9460,9461,9462,9463,9464]
                  </p>
                </div>
              </div>
            </div>

            <div className="p-4 bg-purple-50 border border-purple-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Options (nr_item)</h3>
              <textarea
                name="options"
                value={formData.options}
                onChange={handleChange}
                rows={3}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
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

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" onClick={onClose} variant="outline">
                Hủy
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <>
                    <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                    Đang thêm...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-4 h-4 mr-2 inline" />
                    Thêm Flag
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

interface EditFlagModalProps {
  flag: Flag
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

const EditFlagModal = ({ flag, onClose, onSuccess, onError }: EditFlagModalProps) => {
  const [formData, setFormData] = useState({
    name: flag.name,
    description: flag.description || '',
    icon: flag.icon.toString(),
    gender: flag.gender.toString(),
    level: flag.level.toString(),
    require: flag.require.toString(),
    resale_price: flag.resale_price.toString(),
    part: (flag.part !== null && flag.part !== undefined ? flag.part : -1).toString(),
    options: flag.options || '[]',
    is_up_to_up: flag.is_up_to_up.toString(),
  })
  const [submitting, setSubmitting] = useState(false)
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)

  useEffect(() => {
    if (flag.icon && flag.icon > 0) {
      handleCheckImage()
    }
  }, [])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'options') {
      if (value.trim() === '') {
        setOptionsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setOptionsError('Options phải là một mảng JSON')
          } else {
            setOptionsError(null)
          }
        } catch (err) {
          setOptionsError('JSON không hợp lệ')
        }
      }
    }
  }

  const handleCheckImage = async () => {
    const imageId = parseInt(formData.icon)
    if (!imageId || isNaN(imageId) || imageId < 1) {
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
      if (data.exists && data.imagePath) {
        setImagePreview(data.imagePath)
      } else {
        setImagePreview(null)
      }
    } catch (err) {
      // Ignore
    } finally {
      setCheckingImage(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (optionsError) {
      onError('Vui lòng sửa lỗi JSON options')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch(`/api/flags/${flag.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          icon: parseInt(formData.icon) || 0,
          gender: parseInt(formData.gender) || 3,
          level: parseInt(formData.level) || 0,
          require: parseInt(formData.require) || 0,
          resale_price: parseInt(formData.resale_price) || -1,
          part: parseInt(formData.part) || -1,
          is_up_to_up: parseInt(formData.is_up_to_up) || 0,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Cập nhật flag thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Cập nhật flag thất bại'
      onError(errorMsg)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Sửa Flag</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tên Flag <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mô tả
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={2}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Icon ID <span className="text-red-500">*</span>
              </label>
              <div className="flex gap-2">
                <input
                  type="number"
                  name="icon"
                  value={formData.icon}
                  onChange={handleChange}
                  required
                  min="1"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCheckImage}
                  disabled={checkingImage || !formData.icon}
                >
                  {checkingImage ? (
                    <LoaderIcon className="w-4 h-4 animate-spin" />
                  ) : (
                    'Kiểm tra'
                  )}
                </Button>
              </div>
              {imagePreview && (
                <div className="mt-2 w-20 h-20 border-2 border-gray-300 rounded bg-gray-100 flex items-center justify-center">
                  <img
                    src={imagePreview}
                    alt="Preview"
                    className="w-full h-full object-contain rounded"
                  />
                </div>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Gender
                </label>
                <select
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="0">Trái Đất</option>
                  <option value="1">Namec</option>
                  <option value="2">Xayda</option>
                  <option value="3">Tất cả</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Level
                </label>
                <input
                  type="number"
                  name="level"
                  value={formData.level}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Bag ID (Part) <span className="text-gray-500 text-xs">(Clan Image ID)</span>
              </label>
              <input
                type="number"
                name="part"
                value={formData.part}
                onChange={handleChange}
                min="-1"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="-1 (tự động từ mapping) hoặc Bag ID"
              />
              <p className="text-xs text-gray-500 mt-1">
                Để -1 nếu item ID nằm trong mapping tự động, hoặc nhập Bag ID (Clan Image ID) trực tiếp
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Options (JSON Array)
              </label>
              <textarea
                name="options"
                value={formData.options}
                onChange={handleChange}
                rows={3}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                  optionsError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {optionsError && (
                <p className="text-xs text-red-500 mt-1">{optionsError}</p>
              )}
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" onClick={onClose} variant="outline">
                Hủy
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <>
                    <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                    Đang cập nhật...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-4 h-4 mr-2 inline" />
                    Cập nhật
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

export default function FlagsPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [flags, setFlags] = useState<Flag[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState(searchParams.get('search') || '')
  const [currentPage, setCurrentPage] = useState(parseInt(searchParams.get('page') || '1'))
  const [totalPages, setTotalPages] = useState(1)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [showAddModal, setShowAddModal] = useState(false)
  const [editingFlag, setEditingFlag] = useState<Flag | null>(null)
  const [checkingFlag, setCheckingFlag] = useState<Flag | null>(null)
  const [testingFlagbag, setTestingFlagbag] = useState<number | null>(null)

  useEffect(() => {
    fetchFlags()
  }, [currentPage, searchTerm])

  const fetchFlags = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      params.set('page', currentPage.toString())
      params.set('limit', '20')
      if (searchTerm) {
        params.set('search', searchTerm)
      }

      const response = await fetch(`/api/flags?${params}`)
      const data = await response.json()

      if (data.success) {
        setFlags(data.flags || [])
        setTotalPages(data.totalPages || 1)
      } else {
        setPopup({ message: data.error || 'Lỗi khi tải flags', type: 'error' })
      }
    } catch (error) {
      setPopup({ message: 'Lỗi khi tải flags', type: 'error' })
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (value: string) => {
    setSearchTerm(value)
    setCurrentPage(1)
    const params = new URLSearchParams(searchParams.toString())
    if (value) {
      params.set('search', value)
    } else {
      params.delete('search')
    }
    params.set('page', '1')
    router.push(`/flags?${params}`)
  }

  const handleDelete = async (flag: Flag) => {
    if (!confirm(`Bạn có chắc muốn xóa flag "${flag.name}" (ID: ${flag.id})?`)) {
      return
    }

    try {
      const response = await fetch(`/api/flags/${flag.id}`, {
        method: 'DELETE',
      })
      const data = await response.json()

      if (data.success) {
        setPopup({ message: data.message, type: 'success' })
        fetchFlags()
      } else {
        setPopup({ message: data.error || 'Xóa flag thất bại', type: 'error' })
      }
    } catch (error) {
      setPopup({ message: 'Xóa flag thất bại', type: 'error' })
    }
  }

  const handleCheckFlag = async (flag: Flag) => {
    setCheckingFlag(flag)
    try {
      const response = await fetch(`/api/flags/check/${flag.id}`)
      const data = await response.json()

      if (data.exists) {
        if (data.flag.iconData?.exists) {
          setPopup({
            message: `Flag "${flag.name}" tồn tại. Icon ID ${flag.icon} có ảnh (${data.flag.iconData.size})`,
            type: 'success',
          })
        } else {
          setPopup({
            message: `Flag "${flag.name}" tồn tại nhưng icon ID ${flag.icon} không có ảnh`,
            type: 'error',
          })
        }
      } else {
        setPopup({ message: data.message || 'Flag không tồn tại', type: 'error' })
      }
    } catch (error) {
      setPopup({ message: 'Kiểm tra flag thất bại', type: 'error' })
    } finally {
      setCheckingFlag(null)
    }
  }

  const handleTestFlagbag = (flag: Flag) => {
    if (flag.part !== null && flag.part !== undefined && flag.part !== -1 && flag.part >= 0) {
      setTestingFlagbag(flag.part)
    } else {
      setPopup({
        message: `Item "${flag.name}" (ID: ${flag.id}) không có part field hợp lệ. Part: ${flag.part}`,
        type: 'error',
      })
    }
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
      {showAddModal && (
        <AddFlagModal
          onClose={() => setShowAddModal(false)}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setShowAddModal(false)
            fetchFlags()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
      {editingFlag && (
        <EditFlagModal
          flag={editingFlag}
          onClose={() => setEditingFlag(null)}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setEditingFlag(null)
            fetchFlags()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
      {testingFlagbag !== null && testingFlagbag !== undefined && (
        <FlagbagPreview
          bagId={testingFlagbag}
          onClose={() => setTestingFlagbag(null)}
        />
      )}
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-3xl font-bold text-gray-900">Quản Lý Flags</h1>
              <Button onClick={() => setShowAddModal(true)}>
                <PlusIcon className="w-5 h-5 mr-2" />
                Thêm Flag
              </Button>
            </div>

            <div className="flex gap-4 mb-6">
              <div className="flex-1 relative">
                <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Tìm kiếm theo tên hoặc ID..."
                  value={searchTerm}
                  onChange={(e) => handleSearch(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            {loading ? (
              <div className="flex justify-center items-center py-12">
                <LoaderIcon className="w-8 h-8 animate-spin text-gray-400" />
              </div>
            ) : flags.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">Không tìm thấy flags nào</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {flags.map((flag) => (
                    <FlagCard
                      key={flag.id}
                      flag={flag}
                      onClick={() => setEditingFlag(flag)}
                      onCheck={() => handleCheckFlag(flag)}
                      onDelete={() => handleDelete(flag)}
                      onTestFlagbag={() => handleTestFlagbag(flag)}
                    />
                  ))}
                </div>

                {totalPages > 1 && (
                  <div className="flex justify-center items-center gap-2 mt-6">
                    <Button
                      variant="outline"
                      onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                      disabled={currentPage === 1}
                    >
                      Trước
                    </Button>
                    <span className="px-4 py-2 text-sm text-gray-700">
                      Trang {currentPage} / {totalPages}
                    </span>
                    <Button
                      variant="outline"
                      onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                      disabled={currentPage === totalPages}
                    >
                      Sau
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </>
  )
}


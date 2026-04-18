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
import PetPreview from '@/components/ui/PetPreview'

const GENDERS: Record<number, string> = {
  0: 'Trái Đất',
  1: 'Namec',
  2: 'Xayda',
  3: 'Tất cả',
}

interface Pet {
  id: number
  name: string
  gender: number
  description: string
  level: number
  require: number
  resale_price: number
  icon: number
  head: number
  body: number
  leg: number
  options: string
  is_up_to_up: number
}

interface PetCardProps {
  pet: Pet
  onClick: () => void
  onCheck: () => void
  onDelete: () => void
  onTestPet: () => void
}

function PetCard({ pet, onClick, onCheck, onDelete, onTestPet }: PetCardProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (pet.icon && pet.icon > 0) {
      setLoading(true)
      fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: pet.icon }),
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
  }, [pet.icon])

  return (
    <div
      onClick={onClick}
      className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-4 cursor-pointer border border-gray-200"
    >
      <div className="flex items-start gap-4">
        <div className="w-16 h-16 border-2 border-gray-300 rounded bg-gray-100 flex items-center justify-center flex-shrink-0">
          {loading ? (
            <LoaderIcon className="w-6 h-6 animate-spin text-gray-400" />
          ) : imageUrl ? (
            <img
              src={imageUrl}
              alt={pet.name}
              className="w-full h-full object-contain rounded"
            />
          ) : (
            <ImageIcon className="w-8 h-8 text-gray-400" />
          )}
        </div>
        <div className="flex-1 min-w-0">
          <h3 className="font-semibold text-gray-900 truncate">{pet.name}</h3>
          <p className="text-sm text-gray-600 mt-1 line-clamp-2">
            {pet.description || 'Không có mô tả'}
          </p>
          <div className="flex gap-2 mt-2 text-xs text-gray-500">
            <span>ID: {pet.id}</span>
            <span>•</span>
            <span>Icon: {pet.icon}</span>
            <span>•</span>
            <span>Head: {pet.head}</span>
            <span>•</span>
            <span>Body: {pet.body}</span>
            <span>•</span>
            <span>Leg: {pet.leg}</span>
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              onCheck()
            }}
          >
            <CheckCircleIcon className="w-4 h-4 mr-1" />
            Kiểm tra
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              onTestPet()
            }}
            className="bg-purple-50 hover:bg-purple-100 text-purple-700 border-purple-300"
          >
            <ImageIcon className="w-4 h-4 mr-1" />
            Test Pet
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
  )
}

interface AddPetModalProps {
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

const AddPetModal = ({ onClose, onSuccess, onError }: AddPetModalProps) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    icon: '',
    gender: '3',
    level: '0',
    require: '0',
    resale_price: '-1',
    head: '-1',
    body: '-1',
    leg: '-1',
    partHeadJson: '',
    partBodyJson: '',
    partLegJson: '',
    options: '[]',
    is_up_to_up: '0',
  })
  const [submitting, setSubmitting] = useState(false)
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [partJsonErrors, setPartJsonErrors] = useState<{
    partHead: string | null
    partBody: string | null
    partLeg: string | null
  }>({
    partHead: null,
    partBody: null,
    partLeg: null,
  })
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => {
      const newData = { ...prev, [name]: value }
      
      if (name === 'head' || name === 'body' || name === 'leg') {
        const hasItemValue = (newData.head !== '-1' && newData.head !== '') ||
                             (newData.body !== '-1' && newData.body !== '') ||
                             (newData.leg !== '-1' && newData.leg !== '')
        if (hasItemValue) {
          newData.partHeadJson = ''
          newData.partBodyJson = ''
          newData.partLegJson = ''
          setPartJsonErrors({ partHead: null, partBody: null, partLeg: null })
        }
      }
      
      if (name === 'partHeadJson' || name === 'partBodyJson' || name === 'partLegJson') {
        const hasPartValue = (newData.partHeadJson.trim() !== '') ||
                             (newData.partBodyJson.trim() !== '') ||
                             (newData.partLegJson.trim() !== '')
        if (hasPartValue) {
          newData.head = '-1'
          newData.body = '-1'
          newData.leg = '-1'
        }
        
        if (value.trim() !== '') {
          try {
            const parsed = JSON.parse(value)
            if (!Array.isArray(parsed)) {
              setPartJsonErrors(prev => ({ ...prev, [name.replace('Json', '')]: 'Phải là một mảng JSON' }))
            } else {
              setPartJsonErrors(prev => ({ ...prev, [name.replace('Json', '')]: null }))
            }
          } catch (err) {
            setPartJsonErrors(prev => ({ ...prev, [name.replace('Json', '')]: 'JSON không hợp lệ' }))
          }
        } else {
          setPartJsonErrors(prev => ({ ...prev, [name.replace('Json', '')]: null }))
        }
      }
      
      return newData
    })

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

  const hasItemValues = (formData.head !== '-1' && formData.head !== '') ||
                        (formData.body !== '-1' && formData.body !== '') ||
                        (formData.leg !== '-1' && formData.leg !== '')

  const hasPartValues = (formData.partHeadJson.trim() !== '') ||
                        (formData.partBodyJson.trim() !== '') ||
                        (formData.partLegJson.trim() !== '')

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

    if (hasItemValues && hasPartValues) {
      onError('Không thể điền cả Part ID có sẵn và JSON cùng lúc. Chọn một trong hai cách.')
      return
    }

    if (!hasItemValues && !hasPartValues) {
      onError('Vui lòng chọn một trong hai: Part ID có sẵn hoặc tạo mới từ JSON')
      return
    }

    if (hasPartValues) {
      if (partJsonErrors.partHead || partJsonErrors.partBody || partJsonErrors.partLeg) {
        onError('Vui lòng sửa lỗi JSON part')
        return
      }
    }

    setSubmitting(true)
    try {
      const response = await fetch('/api/pets', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          icon: parseInt(formData.icon) || 0,
          gender: parseInt(formData.gender) || 3,
          level: parseInt(formData.level) || 0,
          require: parseInt(formData.require) || 0,
          resale_price: parseInt(formData.resale_price) || -1,
          head: parseInt(formData.head) || -1,
          body: parseInt(formData.body) || -1,
          leg: parseInt(formData.leg) || -1,
          partHeadJson: formData.partHeadJson.trim(),
          partBodyJson: formData.partBodyJson.trim(),
          partLegJson: formData.partLegJson.trim(),
          is_up_to_up: parseInt(formData.is_up_to_up) || 0,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Thêm pet thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Thêm pet thất bại'
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
            <h2 className="text-2xl font-bold text-gray-900">Thêm Pet Mới</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Thông tin Pet (nr_item)</h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tên Pet <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="VD: Pet Lôi Thần"
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
                    placeholder="Mô tả pet"
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
                      placeholder="VD: 12993"
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
              </div>
            </div>

            <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Part IDs (nr_part)</h3>
              <p className="text-xs text-gray-500 mb-4">
                Chọn một trong hai: Sử dụng Part ID có sẵn hoặc tạo mới từ JSON
              </p>

              <div className="mb-4 p-3 bg-white rounded border border-gray-200">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Cách 1: Sử dụng Part ID có sẵn</h4>
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Head Part ID
                    </label>
                    <input
                      type="number"
                      name="head"
                      value={formData.head}
                      onChange={handleChange}
                      min="-1"
                      disabled={hasPartValues}
                      className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        hasPartValues ? 'bg-gray-100 cursor-not-allowed' : ''
                      }`}
                      placeholder="VD: 2147"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Body Part ID
                    </label>
                    <input
                      type="number"
                      name="body"
                      value={formData.body}
                      onChange={handleChange}
                      min="-1"
                      disabled={hasPartValues}
                      className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        hasPartValues ? 'bg-gray-100 cursor-not-allowed' : ''
                      }`}
                      placeholder="VD: 2148"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Leg Part ID
                    </label>
                    <input
                      type="number"
                      name="leg"
                      value={formData.leg}
                      onChange={handleChange}
                      min="-1"
                      disabled={hasPartValues}
                      className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        hasPartValues ? 'bg-gray-100 cursor-not-allowed' : ''
                      }`}
                      placeholder="VD: 2149"
                    />
                  </div>
                </div>
              </div>

              <div className="p-3 bg-white rounded border border-gray-200">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Cách 2: Tạo mới từ JSON</h4>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Part Head JSON
                    </label>
                    <textarea
                      name="partHeadJson"
                      value={formData.partHeadJson}
                      onChange={handleChange}
                      disabled={hasItemValues}
                      rows={2}
                      className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                        partJsonErrors.partHead ? 'border-red-500' : 'border-gray-300'
                      } ${hasItemValues ? 'bg-gray-100 cursor-not-allowed' : ''}`}
                      placeholder='[{"id":18147,"dx":-2,"dy":-8}]'
                    />
                    {partJsonErrors.partHead && (
                      <p className="text-xs text-red-500 mt-1">{partJsonErrors.partHead}</p>
                    )}
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Part Body JSON
                    </label>
                    <textarea
                      name="partBodyJson"
                      value={formData.partBodyJson}
                      onChange={handleChange}
                      disabled={hasItemValues}
                      rows={2}
                      className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                        partJsonErrors.partBody ? 'border-red-500' : 'border-gray-300'
                      } ${hasItemValues ? 'bg-gray-100 cursor-not-allowed' : ''}`}
                      placeholder='[{"id":18174,"dx":1,"dy":-3}]'
                    />
                    {partJsonErrors.partBody && (
                      <p className="text-xs text-red-500 mt-1">{partJsonErrors.partBody}</p>
                    )}
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Part Leg JSON
                    </label>
                    <textarea
                      name="partLegJson"
                      value={formData.partLegJson}
                      onChange={handleChange}
                      disabled={hasItemValues}
                      rows={2}
                      className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                        partJsonErrors.partLeg ? 'border-red-500' : 'border-gray-300'
                      } ${hasItemValues ? 'bg-gray-100 cursor-not-allowed' : ''}`}
                      placeholder='[{"id":2955,"dx":0,"dy":0}]'
                    />
                    {partJsonErrors.partLeg && (
                      <p className="text-xs text-red-500 mt-1">{partJsonErrors.partLeg}</p>
                    )}
                  </div>
                </div>
                <p className="text-xs text-gray-500 mt-2">
                  Nếu tạo mới, hệ thống sẽ tự động tạo 3 Part ID mới và gán vào head, body, leg
                </p>
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
                    Thêm Pet
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

interface EditPetModalProps {
  pet: Pet
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

const EditPetModal = ({ pet, onClose, onSuccess, onError }: EditPetModalProps) => {
  const [formData, setFormData] = useState({
    name: pet.name,
    description: pet.description || '',
    icon: pet.icon.toString(),
    gender: pet.gender.toString(),
    level: pet.level.toString(),
    require: pet.require.toString(),
    resale_price: pet.resale_price.toString(),
    head: (pet.head !== null && pet.head !== undefined ? pet.head : -1).toString(),
    body: (pet.body !== null && pet.body !== undefined ? pet.body : -1).toString(),
    leg: (pet.leg !== null && pet.leg !== undefined ? pet.leg : -1).toString(),
    options: pet.options || '[]',
    is_up_to_up: pet.is_up_to_up.toString(),
  })
  const [submitting, setSubmitting] = useState(false)
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)

  useEffect(() => {
    if (pet.icon && pet.icon > 0) {
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

    if (!formData.head || !formData.body || !formData.leg) {
      onError('Vui lòng nhập đầy đủ Head, Body, Leg Part ID')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch(`/api/pets/${pet.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          icon: parseInt(formData.icon) || 0,
          gender: parseInt(formData.gender) || 3,
          level: parseInt(formData.level) || 0,
          require: parseInt(formData.require) || 0,
          resale_price: parseInt(formData.resale_price) || -1,
          head: parseInt(formData.head) || -1,
          body: parseInt(formData.body) || -1,
          leg: parseInt(formData.leg) || -1,
          is_up_to_up: parseInt(formData.is_up_to_up) || 0,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Cập nhật pet thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Cập nhật pet thất bại'
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
            <h2 className="text-2xl font-bold text-gray-900">Sửa Pet</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Thông tin Pet (nr_item)</h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tên Pet <span className="text-red-500">*</span>
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
              </div>
            </div>

            <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
              <h3 className="text-sm font-semibold text-gray-700 mb-4">Part IDs (nr_part)</h3>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Head Part ID <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="head"
                    value={formData.head}
                    onChange={handleChange}
                    required
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Body Part ID <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="body"
                    value={formData.body}
                    onChange={handleChange}
                    required
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Leg Part ID <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="leg"
                    value={formData.leg}
                    onChange={handleChange}
                    required
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
              <p className="text-xs text-gray-500 mt-2">
                Part IDs phải tồn tại trong bảng nr_part
              </p>
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
                    Đang cập nhật...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-4 h-4 mr-2 inline" />
                    Cập nhật Pet
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

export default function PetsPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [pets, setPets] = useState<Pet[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState(searchParams.get('search') || '')
  const [currentPage, setCurrentPage] = useState(parseInt(searchParams.get('page') || '1'))
  const [totalPages, setTotalPages] = useState(1)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [showAddModal, setShowAddModal] = useState(false)
  const [editingPet, setEditingPet] = useState<Pet | null>(null)
  const [checkingPet, setCheckingPet] = useState<Pet | null>(null)
  const [testingPet, setTestingPet] = useState<{ headId: number; bodyId: number; legId: number } | null>(null)

  const fetchPets = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      params.set('page', currentPage.toString())
      params.set('limit', '20')
      if (search) {
        params.set('search', search)
      }

      const response = await fetch(`/api/pets?${params.toString()}`)
      const data = await response.json()

      if (data.success) {
        setPets(data.pets || [])
        setTotalPages(data.pagination?.totalPages || 1)
      } else {
        setPopup({ message: data.error || 'Lấy danh sách pet thất bại', type: 'error' })
      }
    } catch (error) {
      setPopup({ message: 'Lỗi khi tải danh sách pet', type: 'error' })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPets()
  }, [currentPage, search])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setCurrentPage(1)
    fetchPets()
  }

  const handleDelete = async (pet: Pet) => {
    if (!confirm(`Bạn có chắc muốn xóa pet "${pet.name}" (ID: ${pet.id})?`)) {
      return
    }

    try {
      const response = await fetch(`/api/pets/${pet.id}`, {
        method: 'DELETE',
      })
      const data = await response.json()

      if (data.success) {
        setPopup({ message: data.message, type: 'success' })
        fetchPets()
      } else {
        setPopup({ message: data.error || 'Xóa pet thất bại', type: 'error' })
      }
    } catch (error) {
      setPopup({ message: 'Xóa pet thất bại', type: 'error' })
    }
  }

  const handleTestPet = (pet: Pet) => {
    if (pet.head && pet.head !== -1 && pet.body && pet.body !== -1 && pet.leg && pet.leg !== -1) {
      setTestingPet({
        headId: pet.head,
        bodyId: pet.body,
        legId: pet.leg,
      })
    } else {
      setPopup({
        message: `Pet "${pet.name}" (ID: ${pet.id}) không có đủ part IDs hợp lệ (Head: ${pet.head}, Body: ${pet.body}, Leg: ${pet.leg})`,
        type: 'error',
      })
    }
  }

  const handleCheckPet = async (pet: Pet) => {
    setCheckingPet(pet)
    try {
      const response = await fetch(`/api/check-image`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: pet.icon }),
      })
      const data = await response.json()

      if (data.exists) {
        if (data.imagePath) {
          setPopup({
            message: `Pet "${pet.name}" tồn tại. Icon ID ${pet.icon} có ảnh`,
            type: 'success',
          })
        } else {
          setPopup({
            message: `Pet "${pet.name}" tồn tại nhưng icon ID ${pet.icon} không có ảnh`,
            type: 'error',
          })
        }
      } else {
        setPopup({
          message: `Pet "${pet.name}" tồn tại nhưng icon ID ${pet.icon} không có ảnh`,
          type: 'error',
        })
      }
    } catch (error) {
      setPopup({ message: 'Kiểm tra pet thất bại', type: 'error' })
    } finally {
      setCheckingPet(null)
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
        <AddPetModal
          onClose={() => setShowAddModal(false)}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setShowAddModal(false)
            fetchPets()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
      {editingPet && (
        <EditPetModal
          pet={editingPet}
          onClose={() => setEditingPet(null)}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setEditingPet(null)
            fetchPets()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
      {testingPet && (
        <PetPreview
          headId={testingPet.headId}
          bodyId={testingPet.bodyId}
          legId={testingPet.legId}
          onClose={() => setTestingPet(null)}
        />
      )}
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-3xl font-bold text-gray-900">Quản lý Pets</h1>
              <Button onClick={() => setShowAddModal(true)}>
                <PlusIcon className="w-5 h-5 mr-2" />
                Thêm Pet
              </Button>
            </div>

            <form onSubmit={handleSearch} className="mb-6">
              <div className="flex gap-2">
                <div className="flex-1 relative">
                  <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    type="text"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    placeholder="Tìm kiếm pet theo tên hoặc mô tả..."
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <Button type="submit">Tìm kiếm</Button>
              </div>
            </form>

            {loading ? (
              <div className="flex justify-center items-center py-12">
                <LoaderIcon className="w-8 h-8 animate-spin text-gray-400" />
              </div>
            ) : pets.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">Không tìm thấy pet nào</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
                  {pets.map((pet) => (
                    <PetCard
                      key={pet.id}
                      pet={pet}
                      onClick={() => setEditingPet(pet)}
                      onCheck={() => handleCheckPet(pet)}
                      onDelete={() => handleDelete(pet)}
                      onTestPet={() => handleTestPet(pet)}
                    />
                  ))}
                </div>

                {totalPages > 1 && (
                  <div className="flex justify-center items-center gap-2">
                    <Button
                      variant="outline"
                      onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                      disabled={currentPage === 1}
                    >
                      Trước
                    </Button>
                    <span className="text-gray-600">
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


'use client'

import { useState } from 'react'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  AlertCircleIcon,
  CheckCircleIcon,
  LoaderIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

export default function AddCostumePage() {
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
  })
  const [partJsonErrors, setPartJsonErrors] = useState<{
    partHead: string | null
    partBody: string | null
    partLeg: string | null
  }>({
    partHead: null,
    partBody: null,
    partLeg: null,
  })
  const [submitting, setSubmitting] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [checkingHeadAvatar, setCheckingHeadAvatar] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => {
      const newData = { ...prev, [name]: value }
      
      // Nếu điền ở nr_item (head/body/leg), clear và disable nr_part
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
      
      // Nếu điền ở nr_part (partHeadJson/partBodyJson/partLegJson), clear và disable nr_item
      if (name === 'partHeadJson' || name === 'partBodyJson' || name === 'partLegJson') {
        const hasPartValue = (newData.partHeadJson.trim() !== '') ||
                             (newData.partBodyJson.trim() !== '') ||
                             (newData.partLegJson.trim() !== '')
        if (hasPartValue) {
          newData.head = '-1'
          newData.body = '-1'
          newData.leg = '-1'
        }
        
        // Validate JSON
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
    if (name === 'icon') {
      setImagePreview(null)
    }
  }
  
  const hasItemValues = (formData.head !== '-1' && formData.head !== '') ||
                        (formData.body !== '-1' && formData.body !== '') ||
                        (formData.leg !== '-1' && formData.leg !== '')
  
  const hasPartValues = (formData.partHeadJson.trim() !== '') ||
                        (formData.partBodyJson.trim() !== '') ||
                        (formData.partLegJson.trim() !== '')

  const handleCheckHeadAvatar = async () => {
    if (!formData.headAvatar || formData.headAvatar === '-1') {
      setPopup({ message: 'Vui lòng nhập Head Avatar trước', type: 'error' })
      return
    }

    const headAvatarId = parseInt(formData.headAvatar)
    if (isNaN(headAvatarId) || headAvatarId < 1) {
      setPopup({ message: 'Head Avatar không hợp lệ', type: 'error' })
      return
    }

    setCheckingHeadAvatar(true)

    try {
      const response = await fetch('/api/check-image', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ imageId: headAvatarId }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Kiểm tra Head Avatar thất bại')
      }

      if (data.exists) {
        setPopup({ 
          message: `Head Avatar ID ${headAvatarId} tồn tại`, 
          type: 'success' 
        })
      } else {
        setPopup({ 
          message: `Head Avatar ID ${headAvatarId} không tồn tại`, 
          type: 'error' 
        })
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Kiểm tra Head Avatar thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setCheckingHeadAvatar(false)
    }
  }

  const handleCheckImage = async () => {
    if (!formData.icon) {
      setPopup({ message: 'Vui lòng nhập ID ảnh trước', type: 'error' })
      return
    }

    const imageId = parseInt(formData.icon)
    if (isNaN(imageId) || imageId < 1) {
      setPopup({ message: 'ID ảnh không hợp lệ', type: 'error' })
      return
    }

    setCheckingImage(true)
    setImagePreview(null)

    try {
      const response = await fetch('/api/check-image', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ imageId }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Kiểm tra ảnh thất bại')
      }

      if (data.exists) {
        const previewPath = data.imagePath
        setImagePreview(previewPath)
        setPopup({ message: data.message, type: 'success' })
      } else {
        setImagePreview(null)
        setPopup({ message: data.message, type: 'error' })
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Kiểm tra ảnh thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setCheckingImage(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.name || !formData.description || !formData.icon) {
      setPopup({ message: 'Vui lòng điền đầy đủ thông tin: tên, mô tả và icon', type: 'error' })
      return
    }

    const icon = parseInt(formData.icon)
    if (isNaN(icon) || icon < 0) {
      setPopup({ message: 'Icon phải là số nguyên dương (ID ảnh)', type: 'error' })
      return
    }

    // Validate JSON nếu có điền
    if (hasPartValues) {
      if (formData.partHeadJson.trim() !== '') {
        try {
          const parsed = JSON.parse(formData.partHeadJson)
          if (!Array.isArray(parsed)) {
            setPopup({ message: 'Part Head JSON phải là một mảng', type: 'error' })
            return
          }
        } catch (err) {
          setPopup({ message: 'Part Head JSON không hợp lệ', type: 'error' })
          return
        }
      }
      if (formData.partBodyJson.trim() !== '') {
        try {
          const parsed = JSON.parse(formData.partBodyJson)
          if (!Array.isArray(parsed)) {
            setPopup({ message: 'Part Body JSON phải là một mảng', type: 'error' })
            return
          }
        } catch (err) {
          setPopup({ message: 'Part Body JSON không hợp lệ', type: 'error' })
          return
        }
      }
      if (formData.partLegJson.trim() !== '') {
        try {
          const parsed = JSON.parse(formData.partLegJson)
          if (!Array.isArray(parsed)) {
            setPopup({ message: 'Part Leg JSON phải là một mảng', type: 'error' })
            return
          }
        } catch (err) {
          setPopup({ message: 'Part Leg JSON không hợp lệ', type: 'error' })
          return
        }
      }
    }

    setSubmitting(true)

    try {
      const response = await fetch('/api/add-costume', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
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
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Thêm cải trang thất bại')
      }

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
      })
      setPartJsonErrors({ partHead: null, partBody: null, partLeg: null })
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Thêm cải trang thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setSubmitting(false)
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
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-3xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6 relative">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-lg">
                <ImageIcon className="w-6 h-6 text-blue-600" />
              </div>
              <div className="flex-1">
                <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Thêm Cải Trang</h1>
                <p className="text-sm text-gray-500">Thêm cải trang mới vào bảng nr_item</p>
              </div>
              {imagePreview && (
                <div className="absolute top-6 right-6 flex flex-col items-end gap-2 z-10">
                  <div className="relative">
                    <img
                      key={imagePreview}
                      src={imagePreview}
                      alt="Preview"
                      className="w-20 h-20 object-contain border-2 border-blue-300 rounded-lg bg-gray-50 shadow-sm"
                      onError={(e) => {
                        console.error('Image load error:', imagePreview)
                        const target = e.target as HTMLImageElement
                        target.style.display = 'none'
                      }}
                      onLoad={() => {
                        console.log('Image loaded successfully:', imagePreview)
                      }}
                    />
                  </div>
                  <span className="text-xs text-gray-500">Preview</span>
                </div>
              )}
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Icon (ID Ảnh) <span className="text-red-500">*</span>
                </label>
                <div className="flex gap-2">
                  <input
                    type="number"
                    name="icon"
                    value={formData.icon}
                    onChange={handleChange}
                    required
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Ví dụ: 1000"
                  />
                  <Button
                    type="button"
                    onClick={handleCheckImage}
                    disabled={!formData.icon || checkingImage}
                    variant="outline"
                    className="min-w-[100px]"
                  >
                    {checkingImage ? (
                      <>
                        <LoaderIcon className="w-4 h-4 mr-2 animate-spin" />
                        Đang kiểm tra...
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

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tên Cải Trang <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Ví dụ: Cải trang Goku SSJ"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Mô Tả <span className="text-red-500">*</span>
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  required
                  rows={3}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Mô tả về cải trang..."
                />
              </div>

              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
                <p className="text-sm font-medium text-yellow-800 mb-2">
                  Chọn một trong hai cách:
                </p>
                <ul className="text-xs text-yellow-700 space-y-1 list-disc list-inside">
                  <li><strong>Cách 1:</strong> Điền Head/Body/Leg (ID) để reference đến part có sẵn trong nr_part</li>
                  <li><strong>Cách 2:</strong> Điền JSON cho Part Head/Body/Leg để tạo part mới, hệ thống sẽ tự động tạo ID và set vào item</li>
                </ul>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Head (ID) - nr_item
                  </label>
                  <input
                    type="number"
                    name="head"
                    value={formData.head}
                    onChange={handleChange}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    }`}
                    placeholder="-1"
                  />
                  {hasPartValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Part, không thể điền ở đây</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Body (ID) - nr_item
                  </label>
                  <input
                    type="number"
                    name="body"
                    value={formData.body}
                    onChange={handleChange}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    }`}
                    placeholder="-1"
                  />
                  {hasPartValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Part, không thể điền ở đây</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Leg (ID) - nr_item
                  </label>
                  <input
                    type="number"
                    name="leg"
                    value={formData.leg}
                    onChange={handleChange}
                    disabled={hasPartValues}
                    className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      hasPartValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    }`}
                    placeholder="-1"
                  />
                  {hasPartValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Part, không thể điền ở đây</p>
                  )}
                </div>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Part Head JSON - nr_part
                  </label>
                  <textarea
                    name="partHeadJson"
                    value={formData.partHeadJson}
                    onChange={handleChange}
                    disabled={hasItemValues}
                    rows={4}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${
                      hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    } ${
                      partJsonErrors.partHead ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder='[{"id":16608,"dx":-2,"dy":-7},{"id":16609,"dx":-2,"dy":-8},{"id":2955,"dx":0,"dy":0}]'
                  />
                  {hasItemValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Item, không thể điền ở đây</p>
                  )}
                  {partJsonErrors.partHead && (
                    <p className="text-xs text-red-500 mt-1">{partJsonErrors.partHead}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Part Body JSON - nr_part
                  </label>
                  <textarea
                    name="partBodyJson"
                    value={formData.partBodyJson}
                    onChange={handleChange}
                    disabled={hasItemValues}
                    rows={4}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${
                      hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    } ${
                      partJsonErrors.partBody ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder='[{"id":16610,"dx":1,"dy":-3},{"id":16611,"dx":-1,"dy":-6},...]'
                  />
                  {hasItemValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Item, không thể điền ở đây</p>
                  )}
                  {partJsonErrors.partBody && (
                    <p className="text-xs text-red-500 mt-1">{partJsonErrors.partBody}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Part Leg JSON - nr_part
                  </label>
                  <textarea
                    name="partLegJson"
                    value={formData.partLegJson}
                    onChange={handleChange}
                    disabled={hasItemValues}
                    rows={4}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${
                      hasItemValues ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    } ${
                      partJsonErrors.partLeg ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder='[{"id":16626,"dx":4,"dy":2},{"id":16627,"dx":0,"dy":-5},...]'
                  />
                  {hasItemValues && (
                    <p className="text-xs text-gray-500 mt-1">Đã điền Item, không thể điền ở đây</p>
                  )}
                  {partJsonErrors.partLeg && (
                    <p className="text-xs text-red-500 mt-1">{partJsonErrors.partLeg}</p>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Head Avatar
                  </label>
                  <div className="flex gap-2">
                    <input
                      type="number"
                      name="headAvatar"
                      value={formData.headAvatar}
                      onChange={handleChange}
                      className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="-1"
                    />
                    <Button
                      type="button"
                      onClick={handleCheckHeadAvatar}
                      disabled={!formData.headAvatar || formData.headAvatar === '-1' || checkingHeadAvatar}
                      variant="outline"
                      className="min-w-[100px]"
                    >
                      {checkingHeadAvatar ? (
                        <>
                          <LoaderIcon className="w-4 h-4 mr-2 animate-spin" />
                          Đang kiểm tra...
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

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Giới Tính
                  </label>
                  <select
                    name="gender"
                    value={formData.gender}
                    onChange={handleChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="0">Nam (0)</option>
                    <option value="1">Nữ (1)</option>
                    <option value="2">Xayda (2)</option>
                    <option value="3">Tất cả (3)</option>
                  </select>
                </div>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <AlertCircleIcon className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                  <div className="text-sm text-gray-700">
                    <p className="font-medium mb-1">Lưu ý:</p>
                    <ul className="list-disc list-inside space-y-1 text-xs">
                      <li>Type sẽ tự động được set là 5 (cải trang)</li>
                      <li>Level và require mặc định là 0</li>
                      <li>Nếu điền Part JSON, hệ thống sẽ tạo 3 part mới (ID liên tiếp) và tự động set vào head/body/leg của item</li>
                      <li>Nếu điền Head/Body/Leg (ID), hệ thống sẽ dùng ID có sẵn từ nr_part</li>
                      <li>ID sẽ tự động được tạo (ID lớn nhất + 1)</li>
                    </ul>
                  </div>
                </div>
              </div>

              <Button
                type="submit"
                disabled={submitting}
                className="w-full py-3 text-base font-semibold"
                size="lg"
              >
                {submitting ? (
                  <>
                    <LoaderIcon className="w-5 h-5 mr-2 animate-spin inline" />
                    Đang thêm...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-5 h-5 mr-2 inline" />
                    Thêm Cải Trang
                  </>
                )}
              </Button>
            </form>
          </div>
        </div>
      </div>
    </>
  )
}


'use client'

import React, { useState, useEffect } from 'react'
import { useSearchParams, useRouter } from 'next/navigation'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  LoaderIcon,
  SearchIcon,
  CheckCircleIcon,
  AlertCircleIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

interface GiftCode {
  id: number
  server_id: number
  code: string
  is_activated: number
  gold: number
  diamond: number
  diamond_lock: number
  items: string
  expires_at: string
  created_at: string
  status?: 'active' | 'expired' | 'not_started'
  isExpired?: boolean
  isNotStarted?: boolean
  isActive?: boolean
}

interface GiftCodeCardProps {
  code: GiftCode
  onClick: () => void
}

function GiftCodeCard({ code, onClick }: GiftCodeCardProps) {
  const statusColors = {
    active: 'bg-green-100 text-green-700',
    expired: 'bg-red-100 text-red-700',
    not_started: 'bg-yellow-100 text-yellow-700',
  }

  const statusLabels = {
    active: 'Hoạt động',
    expired: 'Hết hạn',
    not_started: 'Chưa bắt đầu',
  }

  let itemsParsed: any[] = []
  try {
    itemsParsed = JSON.parse(code.items || '[]')
  } catch {}

  return (
    <div 
      className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow bg-white cursor-pointer"
      onClick={onClick}
    >
      <div className="flex items-start justify-between mb-2">
        <div className="flex-1">
          <h3 className="text-lg font-bold text-gray-900 mb-1">{code.code}</h3>
          <p className="text-xs text-gray-500">ID: {code.id} | Server: {code.server_id}</p>
        </div>
        <span className={`px-2 py-1 text-xs font-medium rounded ${statusColors[code.status || 'active']}`}>
          {statusLabels[code.status || 'active']}
        </span>
      </div>

      <div className="grid grid-cols-2 gap-2 text-sm mt-3">
        {code.gold > 0 && (
          <div className="flex items-center gap-1">
            <span className="text-gray-600">Vàng:</span>
            <span className="font-semibold text-yellow-600">{code.gold.toLocaleString()}</span>
          </div>
        )}
        {code.diamond > 0 && (
          <div className="flex items-center gap-1">
            <span className="text-gray-600">Ngọc:</span>
            <span className="font-semibold text-blue-600">{code.diamond.toLocaleString()}</span>
          </div>
        )}
        {code.diamond_lock > 0 && (
          <div className="flex items-center gap-1">
            <span className="text-gray-600">Hồng ngọc:</span>
            <span className="font-semibold text-pink-600">{code.diamond_lock.toLocaleString()}</span>
          </div>
        )}
        {itemsParsed.length > 0 && (
          <div className="flex items-center gap-1">
            <span className="text-gray-600">Items:</span>
            <span className="font-semibold text-purple-600">{itemsParsed.length}</span>
          </div>
        )}
      </div>

      <div className="mt-2 text-xs text-gray-500">
        <p>Hết hạn: {new Date(code.expires_at).toLocaleString('vi-VN')}</p>
        <p>Tạo: {new Date(code.created_at).toLocaleString('vi-VN')}</p>
      </div>
    </div>
  )
}

export default function GiftCodesPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  
  const [codes, setCodes] = useState<GiftCode[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedCode, setSelectedCode] = useState<GiftCode | null>(null)
  const [showAddModal, setShowAddModal] = useState(false)
  const [showCheckModal, setShowCheckModal] = useState(false)
  const [checkCodeInput, setCheckCodeInput] = useState('')
  const [checkResult, setCheckResult] = useState<any>(null)
  const [checking, setChecking] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  
  const [filters, setFilters] = useState({
    search: searchParams.get('search') || '',
    page: parseInt(searchParams.get('page') || '1'),
  })

  const [pagination, setPagination] = useState({
    page: 1,
    limit: 20,
    total: 0,
    totalPages: 0,
  })

  const loadCodes = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      if (filters.search) params.append('search', filters.search)
      params.append('page', filters.page.toString())

      const response = await fetch(`/api/giftcodes?${params.toString()}`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách giftcode thất bại')
      }

      setCodes(data.codes || [])
      setPagination(data.pagination || { page: 1, limit: 20, total: 0, totalPages: 0 })
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Lấy danh sách giftcode thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCodes()
  }, [filters.page, filters.search])

  useEffect(() => {
    const timer = setTimeout(() => {
      if (filters.search !== searchParams.get('search')) {
        setFilters(prev => ({ ...prev, page: 1, search: searchParams.get('search') || '' }))
      }
    }, 500)
    return () => clearTimeout(timer)
  }, [searchParams])

  const handleSearchChange = (value: string) => {
    setFilters(prev => ({ ...prev, search: value, page: 1 }))
    const params = new URLSearchParams(searchParams.toString())
    if (value) {
      params.set('search', value)
    } else {
      params.delete('search')
    }
    params.delete('page')
    router.push(`/giftcodes?${params.toString()}`)
  }

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }))
    const params = new URLSearchParams(searchParams.toString())
    params.set('page', newPage.toString())
    router.push(`/giftcodes?${params.toString()}`)
  }

  const handleCheckCode = async () => {
    if (!checkCodeInput.trim()) {
      setPopup({ message: 'Vui lòng nhập code', type: 'error' })
      return
    }

    setChecking(true)
    try {
      const response = await fetch(`/api/giftcodes/check?code=${encodeURIComponent(checkCodeInput.trim())}`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Kiểm tra code thất bại')
      }

      setCheckResult(data)
      if (data.exists) {
        setPopup({ message: data.message, type: data.status === 'active' ? 'success' : 'error' })
      } else {
        setPopup({ message: data.message, type: 'error' })
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Kiểm tra code thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setChecking(false)
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
        <div className="max-w-7xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-3">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <ImageIcon className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-900">Quản lý Giftcode</h1>
                  <p className="text-sm text-gray-600">Tổng số: {pagination.total.toLocaleString()} codes</p>
                </div>
              </div>
              <div className="flex gap-2">
                <Button
                  onClick={() => setShowCheckModal(true)}
                  variant="outline"
                >
                  Kiểm tra Code
                </Button>
                <Button
                  onClick={() => setShowAddModal(true)}
                >
                  Thêm Code Mới
                </Button>
              </div>
            </div>

            <div className="mb-6">
              <div className="relative max-w-md">
                <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  value={filters.search}
                  onChange={(e) => handleSearchChange(e.target.value)}
                  placeholder="Tìm kiếm code hoặc ID..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            {loading ? (
              <div className="text-center py-12">
                <LoaderIcon className="w-12 h-12 mx-auto animate-spin text-blue-600" />
                <p className="text-gray-600 mt-4">Đang tải...</p>
              </div>
            ) : codes.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">Không tìm thấy giftcode nào</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
                  {codes.map((code) => (
                    <GiftCodeCard
                      key={code.id}
                      code={code}
                      onClick={() => setSelectedCode(code)}
                    />
                  ))}
                </div>

                {pagination.totalPages > 1 && (
                  <div className="flex items-center justify-between border-t border-gray-200 pt-4">
                    <div className="text-sm text-gray-600">
                      Trang {pagination.page} / {pagination.totalPages} 
                      ({pagination.total.toLocaleString()} codes)
                    </div>
                    <div className="flex gap-2">
                      <Button
                        onClick={() => handlePageChange(pagination.page - 1)}
                        disabled={pagination.page <= 1}
                        variant="outline"
                        size="sm"
                      >
                        Trước
                      </Button>
                      <Button
                        onClick={() => handlePageChange(pagination.page + 1)}
                        disabled={pagination.page >= pagination.totalPages}
                        variant="outline"
                        size="sm"
                      >
                        Sau
                      </Button>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      {showCheckModal && (
        <CheckCodeModal
          codeInput={checkCodeInput}
          setCodeInput={setCheckCodeInput}
          result={checkResult}
          checking={checking}
          onCheck={handleCheckCode}
          onClose={() => {
            setShowCheckModal(false)
            setCheckCodeInput('')
            setCheckResult(null)
          }}
        />
      )}

      {showAddModal && (
        <AddGiftCodeModal
          onClose={() => {
            setShowAddModal(false)
            loadCodes()
          }}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setShowAddModal(false)
            loadCodes()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}

      {selectedCode && (
        <EditGiftCodeModal
          code={selectedCode}
          onClose={() => {
            setSelectedCode(null)
            loadCodes()
          }}
          onSuccess={(message) => {
            setPopup({ message, type: 'success' })
            setSelectedCode(null)
            loadCodes()
          }}
          onError={(message) => {
            setPopup({ message, type: 'error' })
          }}
        />
      )}
    </>
  )
}

interface CheckCodeModalProps {
  codeInput: string
  setCodeInput: (value: string) => void
  result: any
  checking: boolean
  onCheck: () => void
  onClose: () => void
}

function CheckCodeModal({ codeInput, setCodeInput, result, checking, onCheck, onClose }: CheckCodeModalProps) {
  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-md w-full">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Kiểm tra Giftcode</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nhập Code
              </label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={codeInput}
                  onChange={(e) => setCodeInput(e.target.value.toUpperCase())}
                  onKeyPress={(e) => e.key === 'Enter' && !checking && onCheck()}
                  placeholder="VD: KIEMTITNSM"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
                />
                <Button
                  onClick={onCheck}
                  disabled={checking || !codeInput.trim()}
                >
                  {checking ? (
                    <>
                      <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                      Đang kiểm tra...
                    </>
                  ) : (
                    'Kiểm tra'
                  )}
                </Button>
              </div>
            </div>

            {result && (
              <div className={`p-4 rounded-lg border-2 ${
                result.exists && result.status === 'active'
                  ? 'bg-green-50 border-green-200'
                  : 'bg-red-50 border-red-200'
              }`}>
                <div className="flex items-start gap-2">
                  {result.exists && result.status === 'active' ? (
                    <CheckCircleIcon className="w-5 h-5 text-green-600 mt-0.5" />
                  ) : (
                    <AlertCircleIcon className="w-5 h-5 text-red-600 mt-0.5" />
                  )}
                  <div className="flex-1">
                    <p className={`font-medium ${
                      result.exists && result.status === 'active'
                        ? 'text-green-800'
                        : 'text-red-800'
                    }`}>
                      {result.message}
                    </p>
                    {result.exists && result.code && (
                      <div className="mt-3 space-y-2 text-sm">
                        <div className="grid grid-cols-2 gap-2">
                          {result.code.gold > 0 && (
                            <div>
                              <span className="text-gray-600">Vàng: </span>
                              <span className="font-semibold text-yellow-600">{result.code.gold.toLocaleString()}</span>
                            </div>
                          )}
                          {result.code.diamond > 0 && (
                            <div>
                              <span className="text-gray-600">Ngọc: </span>
                              <span className="font-semibold text-blue-600">{result.code.diamond.toLocaleString()}</span>
                            </div>
                          )}
                          {result.code.diamond_lock > 0 && (
                            <div>
                              <span className="text-gray-600">Hồng ngọc: </span>
                              <span className="font-semibold text-pink-600">{result.code.diamond_lock.toLocaleString()}</span>
                            </div>
                          )}
                          {result.code.items && result.code.items.length > 0 && (
                            <div>
                              <span className="text-gray-600">Items: </span>
                              <span className="font-semibold text-purple-600">{result.code.items.length}</span>
                            </div>
                          )}
                        </div>
                        <div className="text-xs text-gray-500 mt-2">
                          <p>Hết hạn: {new Date(result.code.expires_at).toLocaleString('vi-VN')}</p>
                          <p>Tạo: {new Date(result.code.created_at).toLocaleString('vi-VN')}</p>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button onClick={onClose} variant="outline">
                Đóng
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

interface AddGiftCodeModalProps {
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

const AddGiftCodeModal = ({ onClose, onSuccess, onError }: AddGiftCodeModalProps): JSX.Element => {
  const [formData, setFormData] = useState(() => {
    const now = new Date()
    const defaultExpires = new Date(now.getTime() + 365 * 24 * 60 * 60 * 1000)
    const defaultExpiresStr = defaultExpires.toISOString().slice(0, 16)
    return {
      code: '',
      server_id: '1',
      gold: '0',
      diamond: '0',
      diamond_lock: '0',
      items: '[]',
      expires_at: defaultExpiresStr,
      created_at: '',
    }
  })
  const [submitting, setSubmitting] = useState(false)
  const [itemsError, setItemsError] = useState<string | null>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'items') {
      if (value.trim() === '') {
        setItemsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setItemsError('Items phải là một mảng JSON')
          } else {
            setItemsError(null)
          }
        } catch (err) {
          setItemsError('JSON không hợp lệ')
        }
      }
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (itemsError) {
      onError('Vui lòng sửa lỗi JSON items')
      return
    }

    setSubmitting(true)
    try {
      const response = await fetch('/api/giftcodes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          gold: parseInt(formData.gold) || 0,
          diamond: parseInt(formData.diamond) || 0,
          diamond_lock: parseInt(formData.diamond_lock) || 0,
          server_id: parseInt(formData.server_id) || 1,
          expires_at: formData.expires_at || undefined,
          created_at: formData.created_at || undefined,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Tạo giftcode thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Tạo giftcode thất bại'
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
            <h2 className="text-2xl font-bold text-gray-900">Thêm Giftcode Mới</h2>
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
                Code <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="code"
                value={formData.code}
                onChange={handleChange}
                required
                maxLength={30}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
                placeholder="VD: KIEMTITNSM"
              />
              <p className="text-xs text-gray-500 mt-1">5-30 ký tự, tự động chuyển thành chữ hoa</p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Server ID
                </label>
                <input
                  type="number"
                  name="server_id"
                  value={formData.server_id}
                  onChange={handleChange}
                  min="1"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Vàng
                </label>
                <input
                  type="number"
                  name="gold"
                  value={formData.gold}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Ngọc
                </label>
                <input
                  type="number"
                  name="diamond"
                  value={formData.diamond}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Hồng ngọc
                </label>
                <input
                  type="number"
                  name="diamond_lock"
                  value={formData.diamond_lock}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Items (JSON Array)
              </label>
              <textarea
                name="items"
                value={formData.items}
                onChange={handleChange}
                rows={4}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                  itemsError ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder='[{"id":"457","quantity":"10","options":[{"id":30,"param":"0"}]}]'
              />
              {itemsError && (
                <p className="text-xs text-red-500 mt-1">{itemsError}</p>
              )}
              <p className="text-xs text-gray-500 mt-1">
                Format: [{`{"id":"item_id","quantity":"số lượng","options":[...]}`}]
              </p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Thời gian tạo
                </label>
                <input
                  type="datetime-local"
                  name="created_at"
                  value={formData.created_at}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p className="text-xs text-gray-500 mt-1">Để trống = thời gian hiện tại</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Hết hạn
                </label>
                <input
                  type="datetime-local"
                  name="expires_at"
                  value={formData.expires_at}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <p className="text-xs text-gray-500 mt-1">Mặc định: 1 năm từ bây giờ</p>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" onClick={onClose} variant="outline">
                Hủy
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <>
                    <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                    Đang tạo...
                  </>
                ) : (
                  <>
                    <CheckCircleIcon className="w-4 h-4 mr-2 inline" />
                    Tạo Code
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

interface EditGiftCodeModalProps {
  code: GiftCode
  onClose: () => void
  onSuccess: (message: string) => void
  onError: (message: string) => void
}

function EditGiftCodeModal({ code, onClose, onSuccess, onError }: EditGiftCodeModalProps) {
  const [formData, setFormData] = useState({
    code: code.code,
    server_id: code.server_id.toString(),
    gold: code.gold.toString(),
    diamond: code.diamond.toString(),
    diamond_lock: code.diamond_lock.toString(),
    items: code.items,
    expires_at: new Date(code.expires_at).toISOString().slice(0, 16),
    created_at: new Date(code.created_at).toISOString().slice(0, 16),
  })
  const [updating, setUpdating] = useState(false)
  const [itemsError, setItemsError] = useState<string | null>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'items') {
      if (value.trim() === '') {
        setItemsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setItemsError('Items phải là một mảng JSON')
          } else {
            setItemsError(null)
          }
        } catch (err) {
          setItemsError('JSON không hợp lệ')
        }
      }
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (itemsError) {
      onError('Vui lòng sửa lỗi JSON items')
      return
    }

    setUpdating(true)
    try {
      const response = await fetch(`/api/giftcodes/${code.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...formData,
          gold: parseInt(formData.gold) || 0,
          diamond: parseInt(formData.diamond) || 0,
          diamond_lock: parseInt(formData.diamond_lock) || 0,
          server_id: parseInt(formData.server_id) || 1,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Cập nhật giftcode thất bại')
      }

      onSuccess(data.message)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Cập nhật giftcode thất bại'
      onError(errorMsg)
    } finally {
      setUpdating(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Chỉnh sửa Giftcode</h2>
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
                Code <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="code"
                value={formData.code}
                onChange={handleChange}
                required
                maxLength={30}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 uppercase"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Server ID
                </label>
                <input
                  type="number"
                  name="server_id"
                  value={formData.server_id}
                  onChange={handleChange}
                  min="1"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Vàng
                </label>
                <input
                  type="number"
                  name="gold"
                  value={formData.gold}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Ngọc
                </label>
                <input
                  type="number"
                  name="diamond"
                  value={formData.diamond}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Hồng ngọc
                </label>
                <input
                  type="number"
                  name="diamond_lock"
                  value={formData.diamond_lock}
                  onChange={handleChange}
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Items (JSON Array)
              </label>
              <textarea
                name="items"
                value={formData.items}
                onChange={handleChange}
                rows={4}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-xs ${
                  itemsError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {itemsError && (
                <p className="text-xs text-red-500 mt-1">{itemsError}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Thời gian tạo
                </label>
                <input
                  type="datetime-local"
                  name="created_at"
                  value={formData.created_at}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Hết hạn
                </label>
                <input
                  type="datetime-local"
                  name="expires_at"
                  value={formData.expires_at}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" onClick={onClose} variant="outline">
                Hủy
              </Button>
              <Button type="submit" disabled={updating}>
                {updating ? (
                  <>
                    <LoaderIcon className="w-4 h-4 mr-2 animate-spin inline" />
                    Đang cập nhật...
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


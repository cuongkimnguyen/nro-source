'use client'

import { useState, useEffect } from 'react'
import { useSearchParams, useRouter } from 'next/navigation'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  LoaderIcon,
  SearchIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

const ITEM_TYPES: Record<number, string> = {
  0: 'Áo',
  1: 'Quần',
  2: 'Găng tay',
  3: 'Giày',
  4: 'Rada',
  5: 'Tóc',
  6: 'Đầu thân',
  7: 'Sách',
  8: 'Nhiệm vụ',
  9: 'Vàng',
  10: 'Ngọc',
  11: 'Balo',
  12: 'Ngọc rồng',
  13: 'Bùa',
  17: 'Danh hiệu',
  18: 'Pet theo sau',
  19: 'Pet bay',
  21: 'Pet bay bạc 1',
  23: 'Thú cưng 1',
  24: 'Thú cưng 2',
  26: 'Ngọc bội',
  27: 'Vật phẩm phụ trợ',
  34: 'Diamond lock',
  35: 'Hào quang',
  36: 'Cải trang',
  38: 'Pet bay bạc 2',
  39: 'Vật phẩm phụ trợ',
}

const GENDERS: Record<number, string> = {
  0: 'Trái Đất',
  1: 'Namec',
  2: 'Xayda',
  3: 'Tất cả',
}

interface Item {
  id: number
  name: string
  type: number
  gender: number
  description: string
  level: number
  require: number
  resale_price: number
  icon: number
  part: number
  is_up_to_up: number
  head: number
  body: number
  leg: number
  options: string
  mount_id: number
  lock: number
  typeLabel: string
  genderLabel: string
}

interface ItemCardProps {
  item: Item
  onClick: () => void
}

function ItemCard({ item, onClick }: ItemCardProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (item.icon && item.icon > 0) {
      setLoading(true)
      fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: item.icon }),
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
  }, [item.icon])

  return (
    <div 
      className="border border-gray-200 rounded-lg p-3 hover:shadow-md transition-shadow bg-white cursor-pointer"
      onClick={onClick}
    >
      <div className="flex gap-3">
        <div className="w-16 h-16 bg-gray-100 rounded border-2 border-gray-300 flex items-center justify-center flex-shrink-0">
          {loading ? (
            <LoaderIcon className="w-6 h-6 animate-spin text-gray-400" />
          ) : imageUrl ? (
            <img
              src={imageUrl}
              alt={item.name}
              className="w-full h-full object-contain rounded"
              onError={() => setImageUrl(null)}
            />
          ) : (
            <span className="text-xs text-gray-400">No img</span>
          )}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div className="flex-1 min-w-0">
              <h3 className="text-sm font-semibold text-gray-900 truncate" title={item.name}>
                {item.name}
              </h3>
              <p className="text-xs text-gray-500 mt-1">ID: {item.id}</p>
            </div>
          </div>
          <div className="flex flex-wrap gap-2 mt-2">
            <span className="px-2 py-0.5 text-xs bg-blue-100 text-blue-700 rounded">
              {item.typeLabel}
            </span>
            <span className="px-2 py-0.5 text-xs bg-green-100 text-green-700 rounded">
              {item.genderLabel}
            </span>
            {item.level > 0 && (
              <span className="px-2 py-0.5 text-xs bg-purple-100 text-purple-700 rounded">
                Lv.{item.level}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default function ItemsPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  
  const [items, setItems] = useState<Item[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedItem, setSelectedItem] = useState<Item | null>(null)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  
  const [filters, setFilters] = useState({
    type: searchParams.get('type') || 'all',
    gender: searchParams.get('gender') || 'all',
    search: searchParams.get('search') || '',
    page: parseInt(searchParams.get('page') || '1'),
  })

  const [pagination, setPagination] = useState({
    page: 1,
    limit: 50,
    total: 0,
    totalPages: 0,
  })

  const loadItems = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      if (filters.type !== 'all') params.append('type', filters.type)
      if (filters.gender !== 'all') params.append('gender', filters.gender)
      if (filters.search) params.append('search', filters.search)
      params.append('page', filters.page.toString())

      const response = await fetch(`/api/items?${params.toString()}`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách items thất bại')
      }

      setItems(data.items || [])
      setPagination(data.pagination || { page: 1, limit: 50, total: 0, totalPages: 0 })
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Lấy danh sách items thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadItems()
  }, [filters.page, filters.type, filters.gender])

  useEffect(() => {
    const timer = setTimeout(() => {
      if (filters.search !== searchParams.get('search')) {
        setFilters(prev => ({ ...prev, page: 1, search: searchParams.get('search') || '' }))
      }
    }, 500)
    return () => clearTimeout(timer)
  }, [searchParams])

  const handleFilterChange = (key: string, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value, page: 1 }))
    const params = new URLSearchParams(searchParams.toString())
    if (value === 'all') {
      params.delete(key)
    } else {
      params.set(key, value)
    }
    params.delete('page')
    router.push(`/items?${params.toString()}`)
  }

  const handleSearchChange = (value: string) => {
    setFilters(prev => ({ ...prev, search: value, page: 1 }))
    const params = new URLSearchParams(searchParams.toString())
    if (value) {
      params.set('search', value)
    } else {
      params.delete('search')
    }
    params.delete('page')
    router.push(`/items?${params.toString()}`)
  }

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }))
    const params = new URLSearchParams(searchParams.toString())
    params.set('page', newPage.toString())
    router.push(`/items?${params.toString()}`)
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
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-lg">
                <ImageIcon className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Danh sách Items</h1>
                <p className="text-sm text-gray-600">Tổng số: {pagination.total.toLocaleString()} items</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tìm kiếm (Tên hoặc ID)
                </label>
                <div className="relative">
                  <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    type="text"
                    value={filters.search}
                    onChange={(e) => handleSearchChange(e.target.value)}
                    placeholder="Nhập tên hoặc ID item..."
                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Loại (Type)
                </label>
                <select
                  value={filters.type}
                  onChange={(e) => handleFilterChange('type', e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="all">Tất cả</option>
                  {Object.entries(ITEM_TYPES).map(([value, label]) => (
                    <option key={value} value={value}>
                      {label} ({value})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Giới tính (Gender)
                </label>
                <select
                  value={filters.gender}
                  onChange={(e) => handleFilterChange('gender', e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="all">Tất cả</option>
                  {Object.entries(GENDERS).map(([value, label]) => (
                    <option key={value} value={value}>
                      {label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {loading ? (
              <div className="text-center py-12">
                <LoaderIcon className="w-12 h-12 mx-auto animate-spin text-blue-600" />
                <p className="text-gray-600 mt-4">Đang tải...</p>
              </div>
            ) : items.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500">Không tìm thấy items nào</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 mb-6">
                  {items.map((item) => (
                    <ItemCard
                      key={item.id}
                      item={item}
                      onClick={() => setSelectedItem(item)}
                    />
                  ))}
                </div>

                {pagination.totalPages > 1 && (
                  <div className="flex items-center justify-between border-t border-gray-200 pt-4">
                    <div className="text-sm text-gray-600">
                      Trang {pagination.page} / {pagination.totalPages} 
                      ({pagination.total.toLocaleString()} items)
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

      {selectedItem && (
        <ItemDetailModal
          item={selectedItem}
          onClose={() => setSelectedItem(null)}
        />
      )}
    </>
  )
}

interface ItemDetailModalProps {
  item: Item
  onClose: () => void
}

function ItemDetailModal({ item, onClose }: ItemDetailModalProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (item.icon && item.icon > 0) {
      setLoading(true)
      fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: item.icon }),
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
  }, [item.icon])

  let optionsParsed: any[] = []
  try {
    optionsParsed = JSON.parse(item.options || '[]')
  } catch {}

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Chi tiết Item</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          <div className="space-y-4">
            <div className="flex gap-4">
              <div className="w-24 h-24 bg-gray-100 rounded border-2 border-gray-300 flex items-center justify-center flex-shrink-0">
                {loading ? (
                  <LoaderIcon className="w-8 h-8 animate-spin text-gray-400" />
                ) : imageUrl ? (
                  <img
                    src={imageUrl}
                    alt={item.name}
                    className="w-full h-full object-contain rounded"
                    onError={() => setImageUrl(null)}
                  />
                ) : (
                  <span className="text-xs text-gray-400">No img</span>
                )}
              </div>
              <div className="flex-1">
                <h3 className="text-xl font-bold text-gray-900">{item.name}</h3>
                <p className="text-sm text-gray-600 mt-1">ID: {item.id}</p>
                <div className="flex flex-wrap gap-2 mt-2">
                  <span className="px-2 py-1 text-xs bg-blue-100 text-blue-700 rounded">
                    {item.typeLabel}
                  </span>
                  <span className="px-2 py-1 text-xs bg-green-100 text-green-700 rounded">
                    {item.genderLabel}
                  </span>
                  {item.level > 0 && (
                    <span className="px-2 py-1 text-xs bg-purple-100 text-purple-700 rounded">
                      Level {item.level}
                    </span>
                  )}
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                <p className="text-sm text-gray-900">{item.description || 'Không có'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Yêu cầu</label>
                <p className="text-sm text-gray-900">{item.require > 0 ? item.require.toLocaleString() : 'Không có'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Giá bán lại</label>
                <p className="text-sm text-gray-900">{item.resale_price > 0 ? item.resale_price.toLocaleString() : 'Không bán được'}</p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Icon ID</label>
                <p className="text-sm text-gray-900">{item.icon}</p>
              </div>
              {item.part !== -1 && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Part ID</label>
                  <p className="text-sm text-gray-900">{item.part}</p>
                </div>
              )}
              {(item.head !== -1 || item.body !== -1 || item.leg !== -1) && (
                <>
                  {item.head !== -1 && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Head Part ID</label>
                      <p className="text-sm text-gray-900">{item.head}</p>
                    </div>
                  )}
                  {item.body !== -1 && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Body Part ID</label>
                      <p className="text-sm text-gray-900">{item.body}</p>
                    </div>
                  )}
                  {item.leg !== -1 && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Leg Part ID</label>
                      <p className="text-sm text-gray-900">{item.leg}</p>
                    </div>
                  )}
                </>
              )}
            </div>

            {optionsParsed.length > 0 && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Options</label>
                <div className="bg-gray-50 rounded-lg p-3">
                  <pre className="text-xs text-gray-700 overflow-x-auto">
                    {JSON.stringify(optionsParsed, null, 2)}
                  </pre>
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


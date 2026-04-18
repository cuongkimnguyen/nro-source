'use client'

import { useState, useEffect } from 'react'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  AlertCircleIcon,
  CheckCircleIcon,
  LoaderIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

interface Shop {
  table: string
  npc: number
  type: number
  tabs: Array<{ name: string; type: number }>
  buyMore?: number
}

interface ShopItemCardProps {
  item: any
  onClick: () => void
}

function ShopItemCard({ item, onClick }: ShopItemCardProps) {
  const [imageUrl, setImageUrl] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (item.item_icon && item.item_icon > 0) {
      setLoading(true)
      fetch('/api/check-image', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageId: item.item_icon }),
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
  }, [item.item_icon])

  const shopType = item.buy_gold !== undefined ? 0 : 3
  const priceDisplay = shopType === 0
    ? `${item.buy_gold > 0 ? `${item.buy_gold.toLocaleString()} Gold` : ''} ${item.buy_gem > 0 ? `${item.buy_gem} Gem` : ''}`.trim()
    : `${item.buy_special?.toLocaleString() || 0} Special`

  return (
    <div 
      className="border border-gray-200 rounded-lg p-2 hover:shadow-md transition-shadow bg-gray-50 cursor-pointer"
      onClick={onClick}
    >
      <div className="flex flex-col items-center">
        <div className="w-16 h-16 bg-white rounded border-2 border-gray-300 flex items-center justify-center mb-2">
          {loading ? (
            <LoaderIcon className="w-6 h-6 animate-spin text-gray-400" />
          ) : imageUrl ? (
            <img
              src={imageUrl}
              alt={`Item ${item.item_id}`}
              className="w-full h-full object-contain rounded"
              onError={() => setImageUrl(null)}
            />
          ) : (
            <span className="text-xs text-gray-400">No img</span>
          )}
        </div>
        <div className="text-center w-full">
          <p className="text-xs font-medium text-gray-900 truncate" title={item.item_name || `Item ${item.item_id}`}>
            {item.item_name || `ID: ${item.item_id}`}
          </p>
          <p className="text-xs text-gray-600 mt-1">Tab: {item.tab}</p>
          <p className="text-xs text-blue-600 mt-1">{priceDisplay}</p>
          {item.new === 1 && (
            <span className="inline-block mt-1 px-1.5 py-0.5 text-xs bg-green-100 text-green-700 rounded">New</span>
          )}
        </div>
      </div>
    </div>
  )
}

interface EditShopItemPopupProps {
  item: any
  formData: any
  setFormData: (data: any) => void
  optionsError: string | null
  setOptionsError: (error: string | null) => void
  updating: boolean
  selectedShop: Shop | null
  onClose: () => void
  onSave: () => void
}

function EditShopItemPopup({
  item,
  formData,
  setFormData,
  optionsError,
  setOptionsError,
  updating,
  selectedShop,
  onClose,
  onSave,
}: EditShopItemPopupProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData({ ...formData, [name]: value })

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

  const shopType = selectedShop?.type || 0

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Chỉnh sửa Shop Item</h2>
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
                Item ID <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                name="item_id"
                value={formData.item_id}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Tab <span className="text-red-500">*</span>
              </label>
              <select
                name="tab"
                value={formData.tab}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {selectedShop?.tabs.map((tab, index) => (
                  <option key={index} value={index}>
                    Tab {index}: {tab.name}
                  </option>
                ))}
              </select>
            </div>

            {shopType === 0 || shopType === 1 ? (
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Buy Gold
                  </label>
                  <input
                    type="number"
                    name="buy_gold"
                    value={formData.buy_gold}
                    onChange={handleChange}
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Buy Gem
                  </label>
                  <input
                    type="number"
                    name="buy_gem"
                    value={formData.buy_gem}
                    onChange={handleChange}
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            ) : shopType === 3 ? (
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Icon Special
                  </label>
                  <input
                    type="number"
                    name="icon_special"
                    value={formData.icon_special}
                    onChange={handleChange}
                    min="0"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Buy Special <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="number"
                    name="buy_special"
                    value={formData.buy_special}
                    onChange={handleChange}
                    min="0"
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            ) : null}

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
                placeholder='[{"id":31,"param":10}]'
              />
              {optionsError && (
                <p className="text-xs text-red-500 mt-1">{optionsError}</p>
              )}
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Expired
                </label>
                <input
                  type="number"
                  name="expired"
                  value={formData.expired}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  New
                </label>
                <select
                  name="new"
                  value={formData.new}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="0">Không (0)</option>
                  <option value="1">Có (1)</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Preview
                </label>
                <select
                  name="preview"
                  value={formData.preview}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="0">Không (0)</option>
                  <option value="1">Có (1)</option>
                </select>
              </div>
            </div>
          </div>

          <div className="flex gap-3 mt-6">
            <Button
              onClick={onClose}
              variant="outline"
              className="flex-1"
            >
              Hủy
            </Button>
            <Button
              onClick={onSave}
              disabled={updating}
              className="flex-1"
            >
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
        </div>
      </div>
    </div>
  )
}

export default function AddShopItemPage() {
  const [shops, setShops] = useState<Shop[]>([])
  const [selectedShop, setSelectedShop] = useState<Shop | null>(null)
  const [formData, setFormData] = useState({
    table: '',
    item_id: '',
    tab: '0',
    buy_gold: '0',
    buy_gem: '0',
    icon_special: '0',
    buy_special: '0',
    options: '[]',
    expired: '-1',
    new: '0',
    preview: '0',
  })
  const [submitting, setSubmitting] = useState(false)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)
  const [checkingImage, setCheckingImage] = useState(false)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [loadingShops, setLoadingShops] = useState(true)
  const [optionsError, setOptionsError] = useState<string | null>(null)
  const [showItems, setShowItems] = useState(false)
  const [shopItems, setShopItems] = useState<any[]>([])
  const [loadingItems, setLoadingItems] = useState(false)
  const [editingItem, setEditingItem] = useState<any | null>(null)
  const [editFormData, setEditFormData] = useState({
    item_id: '',
    tab: '0',
    buy_gold: '0',
    buy_gem: '0',
    icon_special: '0',
    buy_special: '0',
    options: '[]',
    expired: '-1',
    new: '0',
    preview: '0',
  })
  const [updating, setUpdating] = useState(false)
  const [editOptionsError, setEditOptionsError] = useState<string | null>(null)

  useEffect(() => {
    loadShops()
  }, [])

  useEffect(() => {
    if (formData.table) {
      const shop = shops.find(s => s.table === formData.table)
      setSelectedShop(shop || null)
      if (shop && shop.tabs.length > 0) {
        setFormData(prev => ({ ...prev, tab: '0' }))
      }
    } else {
      setSelectedShop(null)
    }
    setShowItems(false)
    setShopItems([])
  }, [formData.table, shops])

  const loadShopItems = async () => {
    if (!formData.table) return

    setLoadingItems(true)
    try {
      const response = await fetch(`/api/shop-items?table=${encodeURIComponent(formData.table)}`)
      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách items thất bại')
      }

      setShopItems(data.items || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Lấy danh sách items thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setLoadingItems(false)
    }
  }

  const handleToggleItems = () => {
    if (!showItems && shopItems.length === 0) {
      loadShopItems()
    }
    setShowItems(!showItems)
  }

  const loadShops = async () => {
    try {
      const response = await fetch('/api/shops')
      const data = await response.json()
      
      if (!response.ok) {
        throw new Error(data.error || 'Lấy danh sách shop thất bại')
      }

      setShops(data.shops || [])
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Lấy danh sách shop thất bại'
      setPopup({ message: errorMsg, type: 'error' })
    } finally {
      setLoadingShops(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'item_id') {
      setImagePreview(null)
    }

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

  const handleEditChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setEditFormData(prev => ({ ...prev, [name]: value }))

    if (name === 'options') {
      if (value.trim() === '') {
        setEditOptionsError(null)
      } else {
        try {
          const parsed = JSON.parse(value)
          if (!Array.isArray(parsed)) {
            setEditOptionsError('Options phải là một mảng JSON')
          } else {
            setEditOptionsError(null)
          }
        } catch (err) {
          setEditOptionsError('JSON không hợp lệ')
        }
      }
    }
  }

  const handleCheckImage = async () => {
    if (!formData.item_id) {
      setPopup({ message: 'Vui lòng nhập Item ID trước', type: 'error' })
      return
    }

    const itemId = parseInt(formData.item_id)
    if (isNaN(itemId) || itemId < 1) {
      setPopup({ message: 'Item ID không hợp lệ', type: 'error' })
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
        body: JSON.stringify({ imageId: itemId }),
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

    if (!formData.table || !formData.item_id || formData.tab === '') {
      setPopup({ message: 'Vui lòng điền đầy đủ thông tin: shop, item_id và tab', type: 'error' })
      return
    }

    if (optionsError) {
      setPopup({ message: 'Vui lòng sửa lỗi JSON options', type: 'error' })
      return
    }

    const itemId = parseInt(formData.item_id)
    if (isNaN(itemId) || itemId < 0) {
      setPopup({ message: 'Item ID phải là số nguyên dương', type: 'error' })
      return
    }

    const shopType = selectedShop?.type || 0

    if (shopType === 0 || shopType === 1) {
      const buyGold = parseInt(formData.buy_gold) || 0
      const buyGem = parseInt(formData.buy_gem) || 0
      if (buyGold === 0 && buyGem === 0) {
        setPopup({ message: 'Phải nhập ít nhất một trong hai: Buy Gold hoặc Buy Gem', type: 'error' })
        return
      }
    } else if (shopType === 3) {
      const buySpecial = parseInt(formData.buy_special) || 0
      if (buySpecial === 0) {
        setPopup({ message: 'Phải nhập Buy Special', type: 'error' })
        return
      }
    }

    setSubmitting(true)

    try {
      const response = await fetch('/api/add-shop-item', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          table: formData.table,
          item_id: itemId,
          tab: parseInt(formData.tab),
          buy_gold: parseInt(formData.buy_gold) || 0,
          buy_gem: parseInt(formData.buy_gem) || 0,
          icon_special: parseInt(formData.icon_special) || 0,
          buy_special: parseInt(formData.buy_special) || 0,
          options: formData.options.trim() || '[]',
          expired: parseInt(formData.expired) || -1,
          new: parseInt(formData.new) || 0,
          preview: parseInt(formData.preview) || 0,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Thêm shop item thất bại')
      }

      setPopup({ message: data.message, type: 'success' })
      setFormData({
        table: formData.table,
        item_id: '',
        tab: '0',
        buy_gold: '0',
        buy_gem: '0',
        icon_special: '0',
        buy_special: '0',
        options: '[]',
        expired: '-1',
        new: '0',
        preview: '0',
      })
      setImagePreview(null)
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Thêm shop item thất bại'
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
                <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Thêm Item Shop</h1>
                <p className="text-sm text-gray-500">Thêm item mới vào shop</p>
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
                        const target = e.target as HTMLImageElement
                        target.style.display = 'none'
                      }}
                    />
                  </div>
                  <span className="text-xs text-gray-500">Preview</span>
                </div>
              )}
            </div>

            {loadingShops ? (
              <div className="text-center py-8">
                <LoaderIcon className="w-8 h-8 mx-auto animate-spin text-blue-600" />
                <p className="text-gray-600 mt-2">Đang tải danh sách shop...</p>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Shop <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="table"
                    value={formData.table}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="">-- Chọn Shop --</option>
                    {shops.map((shop) => (
                      <option key={shop.table} value={shop.table}>
                        {shop.table} (NPC: {shop.npc}, Type: {shop.type})
                      </option>
                    ))}
                  </select>
                  {selectedShop && (
                    <div className="mt-2 space-y-2">
                      <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg">
                        <p className="text-sm font-medium text-blue-900 mb-1">Thông tin Shop:</p>
                        <p className="text-xs text-blue-700">Type: {selectedShop.type === 0 ? 'Thường (Gold/Gem)' : selectedShop.type === 3 ? 'Đặc biệt (Special)' : 'Skill'}</p>
                        <p className="text-xs text-blue-700">Tabs: {selectedShop.tabs.map((t, i) => `${i}: ${t.name}`).join(', ')}</p>
                      </div>
                      <button
                        type="button"
                        onClick={handleToggleItems}
                        className="w-full p-3 bg-gray-50 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors flex items-center justify-between"
                      >
                        <span className="text-sm font-medium text-gray-700">
                          {showItems ? 'Ẩn' : 'Hiện'} danh sách items trong shop ({shopItems.length > 0 ? shopItems.length : '...'})
                        </span>
                        <span className="text-gray-500">{showItems ? '▲' : '▼'}</span>
                      </button>
                      {showItems && (
                        <div className="border border-gray-200 rounded-lg p-4 bg-white max-h-96 overflow-y-auto">
                          {loadingItems ? (
                            <div className="text-center py-4">
                              <LoaderIcon className="w-6 h-6 mx-auto animate-spin text-blue-600" />
                              <p className="text-sm text-gray-600 mt-2">Đang tải...</p>
                            </div>
                          ) : shopItems.length === 0 ? (
                            <p className="text-sm text-gray-500 text-center py-4">Shop này chưa có items</p>
                          ) : (
                            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                              {shopItems.map((item) => (
                                <ShopItemCard 
                                  key={item.id} 
                                  item={item}
                                  onClick={() => {
                                    setEditingItem(item)
                                    setEditFormData({
                                      item_id: item.item_id.toString(),
                                      tab: item.tab.toString(),
                                      buy_gold: (item.buy_gold || 0).toString(),
                                      buy_gem: (item.buy_gem || 0).toString(),
                                      icon_special: (item.icon_special || 0).toString(),
                                      buy_special: (item.buy_special || 0).toString(),
                                      options: item.options || '[]',
                                      expired: (item.expired || -1).toString(),
                                      new: (item.new || 0).toString(),
                                      preview: (item.preview || 0).toString(),
                                    })
                                    setEditOptionsError(null)
                                  }}
                                />
                              ))}
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Item ID <span className="text-red-500">*</span>
                  </label>
                  <div className="flex gap-2">
                    <input
                      type="number"
                      name="item_id"
                      value={formData.item_id}
                      onChange={handleChange}
                      required
                      className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="Ví dụ: 100"
                    />
                    <Button
                      type="button"
                      onClick={handleCheckImage}
                      disabled={!formData.item_id || checkingImage}
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
                    Tab <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="tab"
                    value={formData.tab}
                    onChange={handleChange}
                    required
                    disabled={!selectedShop}
                    className={`w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                      !selectedShop ? 'bg-gray-100 cursor-not-allowed opacity-60' : ''
                    }`}
                  >
                    {selectedShop ? (
                      selectedShop.tabs.map((tab, index) => (
                        <option key={index} value={index}>
                          Tab {index}: {tab.name} (Type: {tab.type})
                        </option>
                      ))
                    ) : (
                      <option value="">-- Chọn Shop trước --</option>
                    )}
                  </select>
                </div>

                {(selectedShop?.type === 0 || selectedShop?.type === 1) ? (
                  <>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Buy Gold
                        </label>
                        <input
                          type="number"
                          name="buy_gold"
                          value={formData.buy_gold}
                          onChange={handleChange}
                          min="0"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="0"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Buy Gem
                        </label>
                        <input
                          type="number"
                          name="buy_gem"
                          value={formData.buy_gem}
                          onChange={handleChange}
                          min="0"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="0"
                        />
                      </div>
                    </div>
                  </>
                ) : selectedShop?.type === 3 ? (
                  <>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Icon Special
                        </label>
                        <input
                          type="number"
                          name="icon_special"
                          value={formData.icon_special}
                          onChange={handleChange}
                          min="0"
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="0"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Buy Special <span className="text-red-500">*</span>
                        </label>
                        <input
                          type="number"
                          name="buy_special"
                          value={formData.buy_special}
                          onChange={handleChange}
                          min="0"
                          required
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="0"
                        />
                      </div>
                    </div>
                  </>
                ) : null}

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Options (JSON Array)
                  </label>
                  <textarea
                    name="options"
                    value={formData.options}
                    onChange={handleChange}
                    rows={3}
                    className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-xs ${
                      optionsError ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder='[{"id":31,"param":10}]'
                  />
                  {optionsError && (
                    <p className="text-xs text-red-500 mt-1">{optionsError}</p>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Expired
                    </label>
                    <input
                      type="number"
                      name="expired"
                      value={formData.expired}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="-1"
                    />
                    <p className="text-xs text-gray-500 mt-1">-1 = vĩnh viễn</p>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      New
                    </label>
                    <select
                      name="new"
                      value={formData.new}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                      <option value="0">Không (0)</option>
                      <option value="1">Có (1)</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Preview
                    </label>
                    <select
                      name="preview"
                      value={formData.preview}
                      onChange={handleChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                      <option value="0">Không (0)</option>
                      <option value="1">Có (1)</option>
                    </select>
                  </div>
                </div>

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <div className="flex items-start gap-3">
                    <AlertCircleIcon className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                    <div className="text-sm text-gray-700">
                      <p className="font-medium mb-1">Lưu ý:</p>
                      <ul className="list-disc list-inside space-y-1 text-xs">
                        <li>Shop Type 0/1: Phải nhập ít nhất một trong hai Buy Gold hoặc Buy Gem</li>
                        <li>Shop Type 3: Phải nhập Buy Special</li>
                        <li>Expired: -1 = vĩnh viễn, hoặc nhập số giây</li>
                        <li>Options: JSON array format, ví dụ: [{`{"id":31,"param":10}`}]</li>
                      </ul>
                    </div>
                  </div>
                </div>

                <Button
                  type="submit"
                  disabled={submitting || !selectedShop}
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
                      Thêm Item Shop
                    </>
                  )}
                </Button>
              </form>
            )}
          </div>
        </div>
      </div>

      {editingItem && (
        <EditShopItemPopup
          item={editingItem}
          formData={editFormData}
          setFormData={setEditFormData}
          optionsError={editOptionsError}
          setOptionsError={setEditOptionsError}
          updating={updating}
          selectedShop={selectedShop}
          onClose={() => {
            setEditingItem(null)
            setEditFormData({
              item_id: '',
              tab: '0',
              buy_gold: '0',
              buy_gem: '0',
              icon_special: '0',
              buy_special: '0',
              options: '[]',
              expired: '-1',
              new: '0',
              preview: '0',
            })
            setEditOptionsError(null)
          }}
          onSave={async () => {
            if (!editingItem || !formData.table) return

            if (editOptionsError) {
              setPopup({ message: 'Vui lòng sửa lỗi JSON options', type: 'error' })
              return
            }

            const itemId = parseInt(editFormData.item_id)
            if (isNaN(itemId) || itemId < 0) {
              setPopup({ message: 'Item ID phải là số nguyên dương', type: 'error' })
              return
            }

            const shopType = selectedShop?.type || 0

            if (shopType === 0 || shopType === 1) {
              const buyGold = parseInt(editFormData.buy_gold) || 0
              const buyGem = parseInt(editFormData.buy_gem) || 0
              if (buyGold === 0 && buyGem === 0) {
                setPopup({ message: 'Phải nhập ít nhất một trong hai: Buy Gold hoặc Buy Gem', type: 'error' })
                return
              }
            } else if (shopType === 3) {
              const buySpecial = parseInt(editFormData.buy_special) || 0
              if (buySpecial === 0) {
                setPopup({ message: 'Phải nhập Buy Special', type: 'error' })
                return
              }
            }

            setUpdating(true)

            try {
              const response = await fetch('/api/update-shop-item', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                  table: formData.table,
                  id: editingItem.id,
                  item_id: itemId,
                  tab: parseInt(editFormData.tab),
                  buy_gold: parseInt(editFormData.buy_gold) || 0,
                  buy_gem: parseInt(editFormData.buy_gem) || 0,
                  icon_special: parseInt(editFormData.icon_special) || 0,
                  buy_special: parseInt(editFormData.buy_special) || 0,
                  options: editFormData.options.trim() || '[]',
                  expired: parseInt(editFormData.expired) || -1,
                  new: parseInt(editFormData.new) || 0,
                  preview: parseInt(editFormData.preview) || 0,
                }),
              })

              const data = await response.json()

              if (!response.ok) {
                throw new Error(data.error || 'Cập nhật shop item thất bại')
              }

              setPopup({ message: data.message, type: 'success' })
              setEditingItem(null)
              loadShopItems()
            } catch (err) {
              const errorMsg = err instanceof Error ? err.message : 'Cập nhật shop item thất bại'
              setPopup({ message: errorMsg, type: 'error' })
            } finally {
              setUpdating(false)
            }
          }}
        />
      )}
    </>
  )
}


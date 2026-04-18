'use client'

import { useState } from 'react'
import Button from '@/components/ui/Button'
import { 
  ImageIcon, 
  AlertCircleIcon, 
  CheckCircleIcon,
  LoaderIcon,
  InfoIcon,
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

export default function ResizeImagesPage() {
  const [processing, setProcessing] = useState(false)
  const [result, setResult] = useState<{
    success: boolean
    message: string
    stats?: {
      deletedX3: number
      deletedX4: number
      processed: number
      errors: number
      errorDetails?: string[]
    }
  } | null>(null)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)

  const handleResize = async () => {
    if (processing) return

    // Xác nhận trước khi thực hiện
    const confirmed = window.confirm(
      'Bạn có chắc chắn muốn thực hiện thao tác này?\n\n' +
      'Thao tác này sẽ:\n' +
      '1. Xóa toàn bộ ảnh trong thư mục x3 và x4\n' +
      '2. Resize tất cả ảnh x2 (50%) lên 75% (x3) và 100% (x4)\n\n' +
      'Thao tác này không thể hoàn tác!'
    )

    if (!confirmed) return

    setProcessing(true)
    setResult(null)

    try {
      const response = await fetch('/api/resize-x2-to-x3-x4', {
        method: 'POST',
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Xử lý thất bại')
      }

      setResult(data)
      if (data.success) {
        setPopup({ 
          message: data.message, 
          type: 'success' 
        })
      } else {
        setPopup({ 
          message: data.message || 'Xử lý thất bại', 
          type: 'error' 
        })
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Xử lý thất bại'
      setPopup({ message: errorMsg, type: 'error' })
      setResult({
        success: false,
        message: errorMsg,
      })
    } finally {
      setProcessing(false)
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
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-lg">
                <ImageIcon className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Resize Ảnh X2 lên X3, X4</h1>
                <p className="text-sm text-gray-500">Xóa ảnh x3, x4 và resize từ x2</p>
              </div>
            </div>

            <div className="space-y-6">
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <AlertCircleIcon className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
                  <div className="flex-1">
                    <h3 className="font-semibold text-yellow-900 mb-2">Cảnh báo</h3>
                    <p className="text-sm text-yellow-800 mb-2">
                      Thao tác này sẽ:
                    </p>
                    <ul className="text-sm text-yellow-800 list-disc list-inside space-y-1">
                      <li>Xóa <strong>toàn bộ</strong> ảnh trong thư mục x3 và x4</li>
                      <li>Đọc tất cả ảnh từ thư mục x2 (hiện tại là 50% kích thước gốc)</li>
                      <li>Resize mỗi ảnh x2 lên 75% (cho x3) và 100% (cho x4)</li>
                    </ul>
                    <p className="text-sm text-yellow-800 mt-3 font-semibold">
                      ⚠️ Thao tác này không thể hoàn tác!
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <InfoIcon className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                  <div className="flex-1">
                    <h3 className="font-semibold text-blue-900 mb-2">Thông tin</h3>
                    <p className="text-sm text-blue-800">
                      Hệ thống sẽ tự động:
                    </p>
                    <ul className="text-sm text-blue-800 list-disc list-inside space-y-1 mt-2">
                      <li>Xóa tất cả file .png trong thư mục x3/small và x4/small</li>
                      <li>Đọc tất cả ảnh từ thư mục x2/small</li>
                      <li>Resize mỗi ảnh x2 với tỷ lệ 1.5x để tạo x3 (50% × 1.5 = 75%)</li>
                      <li>Resize mỗi ảnh x2 với tỷ lệ 2.0x để tạo x4 (50% × 2.0 = 100%)</li>
                      <li>Lưu các ảnh đã resize vào thư mục tương ứng</li>
                    </ul>
                  </div>
                </div>
              </div>

              {result && (
                <div className={`border rounded-lg p-4 ${
                  result.success 
                    ? 'bg-green-50 border-green-200' 
                    : 'bg-red-50 border-red-200'
                }`}>
                  <div className="flex items-start gap-3">
                    {result.success ? (
                      <CheckCircleIcon className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
                    ) : (
                      <AlertCircleIcon className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
                    )}
                    <div className="flex-1">
                      <h3 className={`font-semibold mb-2 ${
                        result.success ? 'text-green-900' : 'text-red-900'
                      }`}>
                        {result.success ? 'Thành công' : 'Thất bại'}
                      </h3>
                      <p className={`text-sm mb-3 ${
                        result.success ? 'text-green-800' : 'text-red-800'
                      }`}>
                        {result.message}
                      </p>
                      {result.stats && (
                        <div className={`text-sm space-y-1 ${
                          result.success ? 'text-green-800' : 'text-red-800'
                        }`}>
                          <p>• Đã xóa {result.stats.deletedX3} ảnh trong x3</p>
                          <p>• Đã xóa {result.stats.deletedX4} ảnh trong x4</p>
                          <p>• Đã xử lý thành công {result.stats.processed} ảnh</p>
                          {result.stats.errors > 0 && (
                            <>
                              <p>• Gặp lỗi với {result.stats.errors} ảnh</p>
                              {result.stats.errorDetails && result.stats.errorDetails.length > 0 && (
                                <div className="mt-2 pl-4 border-l-2 border-red-300">
                                  <p className="font-semibold mb-1">Chi tiết lỗi:</p>
                                  <ul className="list-disc list-inside space-y-0.5">
                                    {result.stats.errorDetails.map((error, idx) => (
                                      <li key={idx} className="text-xs">{error}</li>
                                    ))}
                                  </ul>
                                </div>
                              )}
                            </>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )}

              <Button
                onClick={handleResize}
                disabled={processing}
                className="w-full py-3 text-base font-semibold"
                size="lg"
                variant={processing ? 'outline' : 'default'}
              >
                {processing ? (
                  <>
                    <LoaderIcon className="w-5 h-5 mr-2 animate-spin inline" />
                    Đang xử lý...
                  </>
                ) : (
                  <>
                    <ImageIcon className="w-5 h-5 mr-2 inline" />
                    Bắt đầu Resize X2 → X3, X4
                  </>
                )}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

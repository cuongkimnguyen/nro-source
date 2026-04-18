import { useEffect } from 'react'
import { CheckCircleIcon, AlertCircleIcon } from './Icons'
import Button from './Button'

interface PopupProps {
  message: string
  type: 'success' | 'error'
  onClose: () => void
  duration?: number
}

export default function Popup({ message, type, onClose, duration }: PopupProps) {
  useEffect(() => {
    if (duration) {
      const timer = setTimeout(() => {
        onClose()
      }, duration)

      return () => clearTimeout(timer)
    }
  }, [onClose, duration])

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4" style={{ zIndex: 9999 }}>
      <div
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />
      
      <div className="relative bg-white rounded-lg shadow-2xl max-w-md w-full animate-popup-in" style={{ position: 'relative', zIndex: 10000 }}>
        <div className="px-8 pt-10 pb-8">
          <div className="flex justify-center mb-6">
            <div className={`w-16 h-16 rounded-full flex items-center justify-center ${
              type === 'success'
                ? 'bg-green-50 border-2 border-green-200'
                : 'bg-red-50 border-2 border-red-200'
            }`}>
              {type === 'success' ? (
                <CheckCircleIcon className={`w-8 h-8 ${
                  type === 'success' ? 'text-green-600' : 'text-red-600'
                }`} />
              ) : (
                <AlertCircleIcon className={`w-8 h-8 ${
                  type === 'success' ? 'text-green-600' : 'text-red-600'
                }`} />
              )}
            </div>
          </div>
          
          <h3 className={`text-center text-xl font-bold mb-3 ${
            type === 'success' ? 'text-gray-900' : 'text-gray-900'
          }`}>
            {type === 'success' ? 'Thành công' : 'Lỗi'}
          </h3>
          
          <p className="text-center text-gray-600 text-base leading-relaxed mb-8">
            {message}
          </p>
          
          <div className="flex items-center justify-between gap-4">
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-gray-700 text-sm font-medium transition-colors"
            >
              Đóng
            </button>
            <Button
              onClick={onClose}
              variant="default"
              size="md"
              className={type === 'success'
                ? 'bg-green-600 hover:bg-green-700 text-white'
                : 'bg-red-600 hover:bg-red-700 text-white'
              }
            >
              Xác nhận
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}


'use client'

import { useState, useEffect } from 'react'
import Button from '@/components/ui/Button'
import { 
  CheckIcon, 
  XIcon, 
  UploadIcon, 
  ImageIcon, 
  AlertCircleIcon, 
  CheckCircleIcon,
  LoaderIcon,
  InfoIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@/components/ui/Icons'
import Popup from '@/components/ui/Popup'

interface UploadResult {
  fileName: string
  imageId: number
  paths: { [key: string]: string }
}

export default function UploadPage() {
  const [files, setFiles] = useState<File[]>([])
  const [previews, setPreviews] = useState<{ file: File; preview: string }[]>([])
  const [startId, setStartId] = useState<string>('')
  const [verified, setVerified] = useState(false)
  const [checking, setChecking] = useState(false)
  const [uploading, setUploading] = useState(false)
  const [uploadProgress, setUploadProgress] = useState<{ current: number; total: number; percent: number } | null>(null)
  const [currentImageIndex, setCurrentImageIndex] = useState(0)
  const [isAutoPlaying, setIsAutoPlaying] = useState(true)
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || [])
    if (selectedFiles.length > 0) {
      setFiles(selectedFiles)
      setVerified(false)
      setCurrentImageIndex(0)
      setIsAutoPlaying(true)

      const newPreviews: { file: File; preview: string }[] = []
      selectedFiles.forEach((file) => {
        const reader = new FileReader()
        reader.onloadend = () => {
          newPreviews.push({ file, preview: reader.result as string })
          if (newPreviews.length === selectedFiles.length) {
            setPreviews(newPreviews)
          }
        }
        reader.readAsDataURL(file)
      })
    }
  }

  useEffect(() => {
    if (previews.length === 0 || !isAutoPlaying) return

    const interval = setInterval(() => {
      setCurrentImageIndex((prev) => (prev + 1) % previews.length)
    }, 2000)

    return () => clearInterval(interval)
  }, [previews.length, isAutoPlaying])

  const handlePrevious = () => {
    setIsAutoPlaying(false)
    setCurrentImageIndex((prev) => (prev === 0 ? previews.length - 1 : prev - 1))
  }

  const handleNext = () => {
    setIsAutoPlaying(false)
    setCurrentImageIndex((prev) => (prev + 1) % previews.length)
  }

  const handleRemoveImage = (index: number) => {
    const newFiles = files.filter((_, i) => i !== index)
    const newPreviews = previews.filter((_, i) => i !== index)
    
    setFiles(newFiles)
    setPreviews(newPreviews)
    setVerified(false)
    
    if (newPreviews.length === 0) {
      setCurrentImageIndex(0)
    } else if (currentImageIndex >= newPreviews.length) {
      setCurrentImageIndex(newPreviews.length - 1)
    }
  }

  const handleCheckIds = async () => {
    if (files.length === 0) {
      setPopup({ message: 'Vui lòng chọn ít nhất một ảnh', type: 'error' })
      return
    }

    if (!startId || startId.trim() === '') {
      setPopup({ message: 'Vui lòng nhập ID bắt đầu', type: 'error' })
      return
    }

    const startIdNum = parseInt(startId)
    if (isNaN(startIdNum) || startIdNum < 1) {
      setPopup({ message: 'ID bắt đầu không hợp lệ. Phải là số từ 1 trở lên', type: 'error' })
      return
    }

    if (startIdNum > 32000) {
      setPopup({ message: 'ID bắt đầu không được vượt quá 32000', type: 'error' })
      return
    }

    setChecking(true)
    setVerified(false)

    try {
      const response = await fetch('/api/check-ids', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          startId: startIdNum,
          count: files.length,
        }),
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || 'Kiểm tra thất bại')
      }

      setVerified(data.available)
      if (data.available) {
        setPopup({ message: `ID từ ${data.startId} đến ${data.endId} đều khả dụng. Bạn có thể tiến hành upload.`, type: 'success' })
      } else {
        setPopup({ message: `Phát hiện ${data.conflicts.length} xung đột tại ID: ${data.conflicts.join(', ')}. Vui lòng chọn ID khác.`, type: 'error' })
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Kiểm tra thất bại'
      setPopup({ message: errorMsg, type: 'error' })
      setVerified(false)
    } finally {
      setChecking(false)
    }
  }

  const handleUpload = async () => {
    if (files.length === 0) {
      setPopup({ message: 'Vui lòng chọn ít nhất một ảnh', type: 'error' })
      return
    }

    if (!startId || startId.trim() === '') {
      setPopup({ message: 'Vui lòng nhập ID bắt đầu', type: 'error' })
      return
    }

    const startIdNum = parseInt(startId)
    if (isNaN(startIdNum) || startIdNum < 1) {
      setPopup({ message: 'ID bắt đầu không hợp lệ. Phải là số từ 1 trở lên', type: 'error' })
      return
    }

    if (startIdNum > 32000) {
      setPopup({ message: 'ID bắt đầu không được vượt quá 32000', type: 'error' })
      return
    }

    if (!verified) {
      setPopup({ message: 'Vui lòng kiểm tra ID trước khi upload', type: 'error' })
      return
    }

    setUploading(true)
    setUploadProgress({ current: 0, total: files.length, percent: 0 })

    const uploadResults: UploadResult[] = []
    const uploadErrors: string[] = []

    const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      const percent = Math.round(((i + 1) / files.length) * 100)
      setUploadProgress({ current: i + 1, total: files.length, percent })

      await sleep(300)

      try {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('fileIndex', i.toString())
        formData.append('startId', startIdNum.toString())

        const response = await fetch('/api/upload', {
          method: 'POST',
          body: formData,
        })

        const data = await response.json()

        if (!response.ok) {
          throw new Error(data.error || 'Upload thất bại')
        }

        uploadResults.push({
          fileName: data.fileName,
          imageId: data.imageId,
          paths: data.paths,
        })

        await sleep(200)
      } catch (err) {
        uploadErrors.push(`${file.name}: ${err instanceof Error ? err.message : 'Upload thất bại'}`)
      }
    }

    setUploading(false)
    setUploadProgress(null)
    
    if (uploadErrors.length > 0) {
      if (uploadResults.length === 0) {
        setPopup({ 
          message: `Upload thất bại. ${uploadErrors.length} ảnh gặp lỗi: ${uploadErrors.slice(0, 3).join(', ')}${uploadErrors.length > 3 ? '...' : ''}`, 
          type: 'error' 
        })
      } else {
        setPopup({ 
          message: `Upload hoàn tất. ${uploadResults.length} ảnh thành công, ${uploadErrors.length} ảnh gặp lỗi`, 
          type: 'error' 
        })
      }
    } else if (uploadResults.length > 0) {
      setPopup({ 
        message: `Upload thành công! ${uploadResults.length} ảnh đã được xử lý và lưu vào hệ thống`, 
        type: 'success' 
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
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-blue-100 rounded-lg">
              <ImageIcon className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Upload Ảnh</h1>
              <p className="text-sm text-gray-500">Tải lên và xử lý nhiều ảnh cùng lúc</p>
            </div>
          </div>

          <div className="space-y-6">
            <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 hover:border-blue-400 transition-colors relative overflow-hidden">
              {previews.length > 0 ? (
                <div className="relative h-80 bg-gray-900 rounded-lg overflow-hidden">
                  <div className="absolute inset-0 flex items-center justify-center">
                    <img
                      src={previews[currentImageIndex]?.preview}
                      alt={`Preview ${currentImageIndex + 1}`}
                      className="max-w-full max-h-full object-contain transition-opacity duration-500"
                      key={currentImageIndex}
                    />
                  </div>
                  
                  <button
                    onClick={() => handleRemoveImage(currentImageIndex)}
                    className="absolute top-3 right-3 bg-red-600 hover:bg-red-700 text-white p-2 rounded-full transition-all z-20 shadow-lg"
                    type="button"
                    aria-label="Xóa ảnh này"
                  >
                    <XIcon className="w-5 h-5" />
                  </button>
                  
                  <div className="absolute top-3 left-3 bg-black/70 text-white px-3 py-1.5 rounded-md text-sm font-medium backdrop-blur-sm">
                    {currentImageIndex + 1} / {previews.length}
                  </div>

                  <button
                    onClick={handlePrevious}
                    className="absolute left-3 top-1/2 -translate-y-1/2 bg-black/70 hover:bg-black/90 text-white p-2.5 rounded-full transition-all z-20 backdrop-blur-sm"
                    type="button"
                    aria-label="Ảnh trước"
                  >
                    <ChevronLeftIcon className="w-6 h-6" />
                  </button>

                  <button
                    onClick={handleNext}
                    className="absolute right-3 top-1/2 -translate-y-1/2 bg-black/70 hover:bg-black/90 text-white p-2.5 rounded-full transition-all z-20 backdrop-blur-sm"
                    type="button"
                    aria-label="Ảnh sau"
                  >
                    <ChevronRightIcon className="w-6 h-6" />
                  </button>

                  <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-1.5 max-w-[90%] overflow-x-auto pb-1">
                    {previews.map((_, index) => (
                      <button
                        key={index}
                        onClick={() => {
                          setIsAutoPlaying(false)
                          setCurrentImageIndex(index)
                        }}
                        className={`h-2 rounded-full transition-all flex-shrink-0 ${
                          index === currentImageIndex
                            ? 'bg-white w-8'
                            : 'bg-white/50 w-2 hover:bg-white/75'
                        }`}
                        type="button"
                        aria-label={`Chuyển đến ảnh ${index + 1}`}
                      />
                    ))}
                  </div>

                  <div className="absolute bottom-3 right-3 bg-black/70 text-white px-2 py-1 rounded text-xs backdrop-blur-sm">
                    {previews[currentImageIndex]?.file.name}
                  </div>
                </div>
              ) : (
                <label className="block cursor-pointer relative">
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={handleFileChange}
                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
                  />
                  <div className="flex flex-col items-center justify-center text-center pointer-events-none min-h-[200px]">
                    <UploadIcon className="w-12 h-12 text-gray-400 mb-3" />
                    <span className="text-sm font-medium text-gray-700 mb-1">
                      Chọn ảnh (có thể chọn nhiều)
                    </span>
                    <span className="text-xs text-gray-500 mb-3">
                      PNG, JPG, GIF hoặc các định dạng ảnh khác
                    </span>
                    <div className="px-4 py-2 border border-gray-300 rounded-md bg-white text-sm font-medium text-gray-700 mt-2">
                      Chọn File
                    </div>
                  </div>
                </label>
              )}
              {files.length > 0 && (
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <p className="text-sm font-medium text-gray-700 mb-2">
                    Đã chọn: <span className="text-blue-600">{files.length}</span> ảnh
                  </p>
                </div>
              )}
            </div>


            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-start gap-3">
                <InfoIcon className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
                <div className="flex-1">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    ID Ảnh Bắt Đầu
                  </label>
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
                    <Button
                      onClick={handleCheckIds}
                      disabled={checking}
                      variant="outline"
                      className="min-w-[100px]"
                    >
                      {checking ? (
                        <>
                          <LoaderIcon className="w-4 h-4 mr-2 animate-spin" />
                          Đang kiểm tra...
                        </>
                      ) : (
                        <>
                          <CheckIcon className="w-4 h-4 mr-2" />
                          Kiểm tra
                        </>
                      )}
                    </Button>
                  </div>
                  {files.length > 0 && startId && !isNaN(parseInt(startId)) && (
                    <p className="mt-2 text-xs text-gray-600">
                      Sẽ sử dụng ID: <span className="font-semibold">{startId}</span> đến{' '}
                      <span className="font-semibold">{parseInt(startId) + files.length - 1}</span> ({files.length} ảnh)
                    </p>
                  )}
                </div>
              </div>
            </div>


            {uploadProgress && (
              <div className="bg-gray-50 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-gray-700">Đang xử lý...</span>
                  <span className="text-sm font-bold text-blue-600">
                    {uploadProgress.percent}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                  <div
                    className="bg-gradient-to-r from-blue-500 to-blue-600 h-3 rounded-full transition-all duration-300"
                    style={{ width: `${uploadProgress.percent}%` }}
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1 text-center">
                  {uploadProgress.current} / {uploadProgress.total} ảnh
                </p>
              </div>
            )}

            <Button
              onClick={handleUpload}
              disabled={files.length === 0 || !verified || uploading}
              className="w-full py-3 text-base font-semibold"
              size="lg"
            >
              {uploading ? (
                <>
                  <LoaderIcon className="w-5 h-5 mr-2 animate-spin inline" />
                  Đang upload {uploadProgress ? `${uploadProgress.percent}%` : ''}...
                </>
              ) : (
                <>
                  <UploadIcon className="w-5 h-5 mr-2 inline" />
                  Upload & Xử Lý {files.length > 0 ? `${files.length} ` : ''}Ảnh
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

'use client'

import { useState, useEffect, useRef } from 'react'
import { LoaderIcon, XIcon } from '@/components/ui/Icons'

interface FlagbagPreviewProps {
  bagId: number | null
  onClose: () => void
}

export default function FlagbagPreview({ bagId, onClose, onBagIdChange }: FlagbagPreviewProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const [clanImage, setClanImage] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [images, setImages] = useState<Map<number, string>>(new Map())
  const [loadedImages, setLoadedImages] = useState<Map<number, HTMLImageElement>>(new Map())
  const [currentFrame, setCurrentFrame] = useState(0)
  const [statusMe, setStatusMe] = useState(1)
  const animationRef = useRef<number>()
  const frameCounterRef = useRef(0)

  useEffect(() => {
    if (bagId !== null && bagId !== undefined && bagId >= 0) {
      fetchClanImage()
    }
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current)
      }
    }
  }, [bagId])

  useEffect(() => {
    if (clanImage && clanImage.images && clanImage.images.length > 0) {
      loadImages()
    }
  }, [clanImage])

  useEffect(() => {
    if (loadedImages.size > 0 && canvasRef.current) {
      startAnimation()
    }
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current)
      }
    }
  }, [loadedImages, statusMe])

  const fetchClanImage = async () => {
    if (bagId === null || bagId === undefined || bagId < 0) return
    
    setLoading(true)
    setError(null)
    try {
      const response = await fetch(`/api/clan-image/${bagId}`)
      const data = await response.json()
      if (data.success) {
        setClanImage(data.clanImage)
      } else {
        setError(data.error || 'Không tìm thấy clan image')
      }
    } catch (err) {
      setError('Lỗi khi tải clan image')
    } finally {
      setLoading(false)
    }
  }

  const loadImages = async () => {
    if (!clanImage || !clanImage.images) return

    const imageMap = new Map<number, string>()
    const loadedImageMap = new Map<number, HTMLImageElement>()
    
    const loadPromises: Promise<void>[] = []
    
    for (const imageId of clanImage.images) {
      try {
        const response = await fetch('/api/check-image', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ imageId }),
        })
        const data = await response.json()
        if (data.exists && data.imagePath) {
          imageMap.set(imageId, data.imagePath)
          
          const loadPromise = new Promise<void>((resolve, reject) => {
            const img = new Image()
            img.crossOrigin = 'anonymous'
            img.onload = () => {
              loadedImageMap.set(imageId, img)
              resolve()
            }
            img.onerror = () => {
              console.error(`Failed to load image ${imageId}`)
              resolve()
            }
            img.src = data.imagePath
          })
          loadPromises.push(loadPromise)
        }
      } catch (err) {
        console.error(`Failed to load image ${imageId}:`, err)
      }
    }

    await Promise.all(loadPromises)
    setImages(imageMap)
    setLoadedImages(loadedImageMap)
  }

  const drawImageOnCanvas = (
    img: HTMLImageElement,
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    num: number,
    num2: number
  ) => {
    const centerX = canvas.width / 2
    const centerY = canvas.height / 2
    const dir = 1

    const x = centerX + (dir === 1 ? -num : num)
    const y = centerY - num2

    ctx.clearRect(0, 0, canvas.width, canvas.height)
    let drawWidth = img.width
    let drawHeight = img.height
    const maxSize = 150
    if (drawWidth > maxSize || drawHeight > maxSize) {
      const scale = Math.min(maxSize / drawWidth, maxSize / drawHeight)
      drawWidth = drawWidth * scale
      drawHeight = drawHeight * scale
    }
    
    ctx.drawImage(img, x - drawWidth / 2, y - drawHeight / 2, drawWidth, drawHeight)
  }

  const calculateOffset = () => {
    let num = 0
    let num2 = 0

    if (statusMe === 6) {
      num = 8
      num2 = 17
    } else if (statusMe === 1) {
      if (frameCounterRef.current % 15 < 5) {
        num = 8
        num2 = 17
      } else {
        num = 8
        num2 = 18
      }
    } else if (statusMe === 2) {
      if (currentFrame <= 3) {
        num = 7
        num2 = 17
      } else {
        num = 7
        num2 = 18
      }
    } else if (statusMe === 3 || statusMe === 9) {
      num = 5
      num2 = 20
    } else if (statusMe === 4) {
      if (currentFrame === 8) {
        num = 5
        num2 = 16
      } else {
        num = 5
        num2 = 20
      }
    } else if (statusMe === 10) {
      if (currentFrame === 8) {
        num = 0
        num2 = 23
      } else {
        num = 5
        num2 = 22
      }
    }

    return { num, num2 }
  }

  const drawFrame = () => {
    const canvas = canvasRef.current
    if (!canvas || !clanImage || !clanImage.images || loadedImages.size === 0) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const imageIds = clanImage.images as number[]
    if (imageIds.length === 0) return

    frameCounterRef.current++
    let frameIndex = Math.floor(frameCounterRef.current / 4) % imageIds.length

    if (imageIds.length > 1 && (frameIndex === 0 || frameIndex === 1) && statusMe !== 1 && statusMe !== 6) {
      frameCounterRef.current = 0
      frameIndex = Math.floor(Date.now() / 100) % 2 === 0 ? 0 : 1
    }
    else if (imageIds.length === 2) {
      frameIndex = 1
    } else if (imageIds.length === 3) {
      if (imageIds[2] >= 0) {
        frameIndex = Math.floor(Date.now() / 100) % 2 === 0 ? 2 : 1
      } else {
        frameIndex = 1
      }
    }

    const imageId = imageIds[frameIndex]
    const img = loadedImages.get(imageId)

    if (img) {
      const { num, num2 } = calculateOffset()
      drawImageOnCanvas(img, ctx, canvas, num, num2)
    }

    setCurrentFrame(frameIndex)
    animationRef.current = requestAnimationFrame(drawFrame)
  }

  const startAnimation = () => {
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current)
    }
    frameCounterRef.current = 0
    drawFrame()
  }

  const statusNames: Record<number, string> = {
    1: 'Đứng',
    2: 'Chạy',
    3: 'Nhảy',
    4: 'Rơi',
    6: 'Không gì',
    9: 'Nhảy (2)',
    10: 'Bay',
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">
              Preview Flagbag
            </h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>


          {loading && (
            <div className="flex justify-center items-center py-12">
              <LoaderIcon className="w-8 h-8 animate-spin text-gray-400" />
            </div>
          )}

          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
              <p className="text-red-700">{error}</p>
            </div>
          )}

          {bagId !== null && bagId !== undefined && (
            <div className="mb-4">
              <p className="text-sm text-gray-600">
                <span className="font-semibold">Bag ID:</span> {bagId}
              </p>
            </div>
          )}

          {clanImage && !loading && (
            <>
              <div className="mb-4">
                <p className="text-sm text-gray-600 mb-2">
                  <span className="font-semibold">Tên:</span> {clanImage.name}
                </p>
                <p className="text-sm text-gray-600 mb-2">
                  <span className="font-semibold">Images:</span> [{clanImage.images.join(', ')}]
                </p>
                <p className="text-sm text-gray-600">
                  <span className="font-semibold">Frame hiện tại:</span> {currentFrame + 1}/{clanImage.images.length}
                </p>
              </div>

              <div className="mb-4 flex gap-2 flex-wrap">
                {Object.entries(statusNames).map(([key, name]) => (
                  <button
                    key={key}
                    onClick={() => {
                      setStatusMe(parseInt(key))
                      frameCounterRef.current = 0
                    }}
                    className={`px-3 py-1 rounded text-sm ${
                      statusMe === parseInt(key)
                        ? 'bg-blue-500 text-white'
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                    }`}
                  >
                    {name}
                  </button>
                ))}
              </div>

              <div className="bg-gray-100 rounded-lg p-4 flex justify-center items-center min-h-[400px]">
                <div className="relative">
                  <canvas
                    ref={canvasRef}
                    width={200}
                    height={400}
                    className="border-2 border-gray-300 bg-white"
                  />
                  <div className="absolute top-2 left-2 bg-black/50 text-white text-xs px-2 py-1 rounded">
                    Frame: {currentFrame + 1}/{clanImage.images.length}
                  </div>
                </div>
              </div>

              {loadedImages.size === 0 && clanImage.images.length > 0 && (
                <div className="mt-4 text-center text-gray-500">
                  <LoaderIcon className="w-6 h-6 animate-spin inline mr-2" />
                  Đang tải images...
                </div>
              )}

              {loadedImages.size > 0 && (
                <div className="mt-4">
                  <p className="text-sm font-semibold text-gray-700 mb-2">Loaded Images:</p>
                  <div className="flex gap-2 flex-wrap">
                    {clanImage.images.map((imageId: number, idx: number) => {
                      const imageSrc = images.get(imageId)
                      return (
                        <div key={imageId} className="relative">
                          <div className="w-16 h-16 border-2 border-gray-300 rounded bg-white flex items-center justify-center">
                            {imageSrc ? (
                              <img
                                src={imageSrc}
                                alt={`Image ${imageId}`}
                                className="w-full h-full object-contain"
                              />
                            ) : (
                              <span className="text-xs text-gray-400">No img</span>
                            )}
                          </div>
                          <div className="absolute -top-2 -right-2 bg-blue-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                            {idx}
                          </div>
                        </div>
                      )
                    })}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}


'use client'

import { useState, useEffect, useRef } from 'react'
import { LoaderIcon } from '@/components/ui/Icons'

interface PetPreviewProps {
  headId: number
  bodyId: number
  legId: number
  onClose: () => void
}

const CharInfo: number[][][] = [
  [[0, -13, 34], [1, -8, 10], [1, -9, 16], [1, -9, 45]],
  [[0, -13, 35], [1, -8, 10], [1, -9, 17], [1, -9, 46]],
  [[1, -10, 33], [2, -10, 11], [2, -8, 16], [1, -12, 49]],
  [[1, -10, 32], [3, -12, 10], [3, -11, 15], [1, -13, 47]],
  [[1, -10, 34], [4, -8, 11], [4, -7, 17], [1, -12, 47]],
  [[1, -10, 34], [5, -12, 11], [5, -9, 17], [1, -13, 49]],
  [[1, -10, 33], [6, -10, 10], [6, -8, 16], [1, -12, 47]],
  [[0, -9, 36], [7, -5, 17], [7, -11, 25], [1, -8, 49]],
  [[0, -7, 35], [0, -18, 22], [7, -10, 25], [1, -7, 48]],
  [[1, -11, 35], [10, -3, 25], [12, -10, 26], [0, 0, 0]],
  [[1, -11, 37], [11, -3, 25], [12, -11, 27], [0, 0, 0]],
  [[0, -14, 34], [12, -8, 21], [9, -7, 31], [0, 0, 0]],
  [[0, -12, 35], [8, -5, 14], [8, -15, 29], [1, -9, 49]],
  [[1, -9, 34], [9, -12, 9], [10, -7, 19], [0, 0, 0]],
  [[1, -13, 34], [9, -12, 9], [11, -10, 19], [0, 0, 0]],
  [[1, -8, 32], [9, -12, 9], [2, -6, 15], [0, 0, 0]],
  [[1, -8, 32], [9, -12, 9], [13, -12, 16], [0, 0, 0]],
  [[0, -10, 31], [9, -12, 9], [7, -13, 20], [0, 0, 0]],
  [[0, -11, 32], [9, -12, 9], [8, -15, 26], [0, 0, 0]],
  [[0, -9, 33], [9, -12, 9], [14, -8, 18], [0, 0, 0]],
  [[0, -11, 33], [9, -12, 9], [15, -6, 19], [0, 0, 0]],
  [[1, -10, 34], [16, -11, 10], [16, -8, 16], [1, -12, 47]],
  [[1, -10, 34], [16, -11, 10], [16, -8, 17], [1, -13, 49]],
  [[0, -13, 35], [1, -8, 10], [1, -9, 17], [1, -9, 46]],
  [[1, -10, 33], [16, -11, 10], [16, -8, 16], [1, -12, 47]],
  [[1, -10, 34], [16, -11, 10], [16, -8, 17], [1, -13, 49]],
  [[0, -7, 35], [0, -18, 22], [7, -10, 25], [1, -7, 48]],
  [[1, -11, 35], [10, -3, 25], [12, -10, 26], [0, 0, 0]],
  [[1, -11, 37], [11, -3, 25], [12, -11, 27], [0, 0, 0]],
  [[0, -14, 34], [12, -8, 21], [9, -7, 31], [0, 0, 0]],
  [[0, -12, 35], [8, -5, 14], [8, -15, 29], [1, -9, 49]],
  [[1, -9, 34], [9, -12, 9], [10, -7, 19], [0, 0, 0]],
  [[1, -13, 34], [9, -12, 9], [11, -10, 19], [0, 0, 0]],
]

export default function PetPreview({ headId, bodyId, legId, onClose }: PetPreviewProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const [parts, setParts] = useState<{ head: any; body: any; leg: any } | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [loadedImages, setLoadedImages] = useState<Map<number, HTMLImageElement>>(new Map())
  const [currentFrame, setCurrentFrame] = useState(0)
  const statusMe = 2
  const animationRef = useRef<number>()
  const frameCounterRef = useRef(0)

  useEffect(() => {
    fetchParts()
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current)
      }
    }
  }, [headId, bodyId, legId])

  useEffect(() => {
    if (parts && parts.head && parts.body && parts.leg) {
      loadImages()
    }
  }, [parts])

  useEffect(() => {
    if (loadedImages.size > 0 && canvasRef.current) {
      startAnimation()
    }
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current)
      }
    }
  }, [loadedImages])

  const fetchParts = async () => {
    setLoading(true)
    setError(null)
    try {
      const [headRes, bodyRes, legRes] = await Promise.all([
        fetch(`/api/part/${headId}`),
        fetch(`/api/part/${bodyId}`),
        fetch(`/api/part/${legId}`),
      ])

      const headData = await headRes.json()
      const bodyData = await bodyRes.json()
      const legData = await legRes.json()

      if (headData.success && bodyData.success && legData.success) {
        setParts({
          head: headData.part,
          body: bodyData.part,
          leg: legData.part,
        })
      } else {
        setError('Không tìm thấy một hoặc nhiều part')
      }
    } catch (err) {
      setError('Lỗi khi tải part data')
    } finally {
      setLoading(false)
    }
  }

  const loadImages = async () => {
    if (!parts) return

    const allImageIds = new Set<number>()
    const partDataArray = [parts.head.part, parts.body.part, parts.leg.part]
    
    partDataArray.forEach(partData => {
      if (Array.isArray(partData)) {
        partData.forEach((item: any) => {
          if (item.id) allImageIds.add(item.id)
        })
      }
    })

    const loadedImageMap = new Map<number, HTMLImageElement>()
    const loadPromises: Promise<void>[] = []

    for (const imageId of allImageIds) {
      try {
        const response = await fetch('/api/check-image', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ imageId }),
        })
        const data = await response.json()
        if (data.exists && data.imagePath) {
          const loadPromise = new Promise<void>((resolve) => {
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
    setLoadedImages(loadedImageMap)
  }

  const drawFrame = () => {
    const canvas = canvasRef.current
    if (!canvas || !parts || loadedImages.size === 0) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    const centerX = canvas.width / 2
    const centerY = canvas.height / 2
    const direction = 1
    const num = direction === 1 ? 0 : 2
    const anchor = direction === 1 ? 0 : 24

    frameCounterRef.current++
    
    let cf = 0
    if (statusMe === 1) {
      cf = 0
    } else if (statusMe === 2) {
      const runFrames = [1, 2, 3, 4, 5, 6, 7]
      const frameIndex = Math.floor(frameCounterRef.current / 4) % runFrames.length
      cf = runFrames[frameIndex]
    } else if (statusMe === 3 || statusMe === 9) {
      cf = 8
    } else if (statusMe === 4 || statusMe === 10) {
      cf = 9
    } else {
      cf = 0
    }

    if (cf >= CharInfo.length) return

    const headPart = parts.head.part
    const legPart = parts.leg.part
    const bodyPart = parts.body.part

    if (!Array.isArray(headPart) || !Array.isArray(legPart) || !Array.isArray(bodyPart)) return

    const headInfo = CharInfo[cf][0]
    const legInfo = CharInfo[cf][1]
    const bodyInfo = CharInfo[cf][2]

    if (!headInfo || !legInfo || !bodyInfo) return

    const headPiIndex = headInfo[0] < headPart.length ? headInfo[0] : 0
    const legPiIndex = legInfo[0] < legPart.length ? legInfo[0] : 0
    const bodyPiIndex = bodyInfo[0] < bodyPart.length ? bodyInfo[0] : 0

    const headPi = headPart[headPiIndex]
    const legPi = legPart[legPiIndex]
    const bodyPi = bodyPart[bodyPiIndex]

    if (!headPi || !legPi || !bodyPi) return

    const drawPart = (pi: any, info: number[], offsetY: number = 0) => {
      const img = loadedImages.get(pi.id)
      if (!img) return

      const x = centerX + (info[1] + (pi.dx || 0)) * (direction === 1 ? 1 : -1)
      const y = centerY - info[2] + (pi.dy || 0) + offsetY

      let drawWidth = img.width
      let drawHeight = img.height
      const maxSize = 200
      if (drawWidth > maxSize || drawHeight > maxSize) {
        const scale = Math.min(maxSize / drawWidth, maxSize / drawHeight)
        drawWidth = drawWidth * scale
        drawHeight = drawHeight * scale
      }

      ctx.save()
      if (direction === -1) {
        ctx.translate(x, y)
        ctx.scale(-1, 1)
        ctx.drawImage(img, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight)
        ctx.restore()
      } else {
        ctx.drawImage(img, x - drawWidth / 2, y - drawHeight / 2, drawWidth, drawHeight)
      }
    }

    drawPart(legPi, legInfo)
    drawPart(bodyPi, bodyInfo)
    drawPart(headPi, headInfo)

    setCurrentFrame(cf)
    animationRef.current = requestAnimationFrame(drawFrame)
  }

  const startAnimation = () => {
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current)
    }
    frameCounterRef.current = 0
    drawFrame()
  }

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Preview Pet</h2>
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

          {parts && !loading && (
            <div className="space-y-4">
              <div className="text-sm text-gray-600 space-y-1">
                <p>
                  <span className="font-semibold">Head Part ID:</span> {headId}
                </p>
                <p>
                  <span className="font-semibold">Body Part ID:</span> {bodyId}
                </p>
                <p>
                  <span className="font-semibold">Leg Part ID:</span> {legId}
                </p>
              </div>

              <div className="flex justify-center bg-gray-100 rounded-lg p-4">
                <canvas
                  ref={canvasRef}
                  width={400}
                  height={500}
                  className="bg-white border border-gray-300 rounded"
                />
              </div>

            </div>
          )}
        </div>
      </div>
    </div>
  )
}

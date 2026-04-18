import sharp from 'sharp'
import { promises as fs } from 'fs'
import path from 'path'

export interface ResizeOptions {
  width?: number
  height?: number
  scale: number
}

export async function resizeImage(
  inputBuffer: Buffer,
  outputPath: string,
  scale: number,
  allowEnlargement: boolean = false
): Promise<void> {
  const image = sharp(inputBuffer)
  const metadata = await image.metadata()
  
  const newWidth = metadata.width ? Math.round(metadata.width * scale) : undefined
  const newHeight = metadata.height ? Math.round(metadata.height * scale) : undefined

  await image
    .resize(newWidth, newHeight, {
      fit: 'inside',
      withoutEnlargement: !allowEnlargement,
    })
    .png()
    .toFile(outputPath)
}

export async function processImageToAllSizes(
  inputBuffer: Buffer,
  baseFileName: string,
  outputDir: string,
  settings: any
): Promise<{ [key: string]: string }> {
  const sizes = settings.getActiveSizes()

  const results: { [key: string]: string } = {}

  for (const size of sizes) {
    const folderPath = path.join(outputDir, size.folder, 'small')
    await fs.mkdir(folderPath, { recursive: true })
    
    const outputPath = path.join(folderPath, baseFileName)
    await resizeImage(inputBuffer, outputPath, size.scale)
    results[size.folder] = outputPath
  }

  return results
}

export function getAllGaps(
  existingIds: number[],
  maxId: number = 32000
): { start: number; end: number; size: number }[] {
  if (existingIds.length === 0) {
    return [{ start: 1, end: maxId, size: maxId }]
  }

  const sortedIds = [...existingIds].sort((a, b) => a - b)
  const gaps: { start: number; end: number; size: number }[] = []

  if (sortedIds[0] > 1) {
    const gapSize = sortedIds[0] - 1
    gaps.push({ start: 1, end: sortedIds[0] - 1, size: gapSize })
  }

  for (let i = 0; i < sortedIds.length - 1; i++) {
    const gapSize = sortedIds[i + 1] - sortedIds[i] - 1
    if (gapSize > 0) {
      gaps.push({
        start: sortedIds[i] + 1,
        end: sortedIds[i + 1] - 1,
        size: gapSize,
      })
    }
  }

  const lastId = sortedIds[sortedIds.length - 1]
  if (lastId < maxId) {
    const gapSize = maxId - lastId
    gaps.push({
      start: lastId + 1,
      end: maxId,
      size: gapSize,
    })
  }

  return gaps.sort((a, b) => b.size - a.size)
}

export async function findAvailableGapForCount(
  existingIds: number[],
  count: number,
  baseDir: string,
  settings: any,
  maxId: number = 32000
): Promise<{ start: number; end: number } | null> {
  if (count <= 0) return null

  const gaps = getAllGaps(existingIds, maxId)

  for (const gap of gaps) {
    if (gap.size < count) continue

    for (let startId = gap.start; startId <= gap.end - count + 1; startId++) {
      let allAvailable = true
      for (let i = 0; i < count; i++) {
        const checkId = startId + i
        if (checkId > maxId) {
          allAvailable = false
          break
        }
        const fileName = formatImageId(checkId)
        const exists = await checkFileNameExists(fileName, baseDir, settings)
        if (exists) {
          allAvailable = false
          break
        }
      }

      if (allAvailable) {
        return { start: startId, end: startId + count - 1 }
      }
    }
  }

  return null
}

export function findGapForCount(
  existingIds: number[],
  count: number,
  maxId: number = 32000
): { start: number; end: number } | null {
  if (existingIds.length === 0) {
    if (count <= maxId) {
      return { start: 1, end: count }
    }
    return null
  }

  const gaps = getAllGaps(existingIds, maxId)

  for (const gap of gaps) {
    if (gap.size >= count) {
      return { start: gap.start, end: gap.start + count - 1 }
    }
  }

  return null
}

export function findLargestGap(existingIds: number[], maxId: number = 32000): { start: number; end: number; size: number } | null {
  if (existingIds.length === 0) {
    return { start: 1, end: maxId, size: maxId }
  }

  const sortedIds = [...existingIds].sort((a, b) => a - b)
  const gaps: { start: number; end: number; size: number }[] = []

  if (sortedIds[0] > 1) {
    gaps.push({ start: 1, end: sortedIds[0] - 1, size: sortedIds[0] - 1 })
  }

  for (let i = 0; i < sortedIds.length - 1; i++) {
    const gap = sortedIds[i + 1] - sortedIds[i] - 1
    if (gap > 0) {
      gaps.push({
        start: sortedIds[i] + 1,
        end: sortedIds[i + 1] - 1,
        size: gap,
      })
    }
  }

  const lastId = sortedIds[sortedIds.length - 1]
  if (lastId < maxId) {
    gaps.push({
      start: lastId + 1,
      end: maxId,
      size: maxId - lastId,
    })
  }

  if (gaps.length === 0) return null

  gaps.sort((a, b) => b.size - a.size)
  return gaps[0]
}

export function generateNextImageId(existingIds: number[], maxId: number = 32000): number {
  if (existingIds.length === 0) return 1

  const gap = findLargestGap(existingIds, maxId)
  if (gap) {
    return gap.start
  }

  const maxExisting = Math.max(...existingIds)
  if (maxExisting >= maxId) {
    throw new Error(`Maximum image ID (${maxId}) reached`)
  }

  return maxExisting + 1
}

export function formatImageId(id: number): string {
  return `Small${id.toString().padStart(4, '0')}.png`
}

export async function checkFileNameExists(
  fileName: string,
  baseDir: string,
  settings: any
): Promise<boolean> {
  const activeSizes = settings.getActiveSizes()
  
  for (const size of activeSizes) {
    const filePath = path.join(baseDir, size.folder, 'small', fileName)
    try {
      await fs.access(filePath)
      return true
    } catch {
      continue
    }
  }
  return false
}


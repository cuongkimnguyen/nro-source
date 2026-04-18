'use client'

import { useMemo, useState } from 'react'
import Button from '@/components/ui/Button'
import Popup from '@/components/ui/Popup'
import { CheckCircleIcon, AlertCircleIcon, SaveIcon } from '@/components/ui/Icons'

type OldTriple = [number, number, number]
type NewObj = { id: number; dx: number; dy: number }

function normalizeInputToJson(text: string) {
  return text
    .trim()
    .replace(/\r\n/g, '\n')
    .replace(/^\uFEFF/, '')
}

function parseOldFormat(text: string): OldTriple[] {
  const normalized = normalizeInputToJson(text)
  const parsed = JSON.parse(normalized)
  if (!Array.isArray(parsed)) throw new Error('Dữ liệu phải là một mảng')

  const triples: OldTriple[] = parsed.map((row: any, idx: number) => {
    if (!Array.isArray(row) || row.length !== 3) {
      throw new Error(`Phần tử #${idx + 1} phải là mảng 3 phần tử [id, dx, dy]`)
    }
    const [id, dx, dy] = row
    if (![id, dx, dy].every((v) => Number.isFinite(Number(v)))) {
      throw new Error(`Phần tử #${idx + 1} phải là số: [id, dx, dy]`)
    }
    return [Number(id), Number(dx), Number(dy)]
  })

  return triples
}

function convertToNewFormat(triples: OldTriple[]): NewObj[] {
  return triples.map(([id, dx, dy]) => ({ dx, dy, id }))
}

function prettyJson(value: any) {
  return JSON.stringify(value, null, 2)
}

export default function ConvertPartPage() {
  const [input, setInput] = useState('[[15868,-2,-27],[15871,-2,-25],[2955,0,0]]')
  const [popup, setPopup] = useState<{ message: string; type: 'success' | 'error' } | null>(null)

  const { output, error } = useMemo(() => {
    try {
      if (input.trim() === '') return { output: '', error: null as string | null }
      const triples = parseOldFormat(input)
      const converted = convertToNewFormat(triples)
      return { output: prettyJson(converted), error: null as string | null }
    } catch (e) {
      const msg = e instanceof Error ? e.message : 'Dữ liệu không hợp lệ'
      return { output: '', error: msg }
    }
  }, [input])

  const handleCopy = async () => {
    if (!output) return
    try {
      await navigator.clipboard.writeText(output)
      setPopup({ message: 'Đã copy kết quả', type: 'success' })
    } catch {
      setPopup({ message: 'Copy thất bại (trình duyệt không cho phép)', type: 'error' })
    }
  }

  const handleClear = () => {
    setInput('')
  }

  return (
    <>
      {popup && <Popup message={popup.message} type={popup.type} onClose={() => setPopup(null)} />}
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-4 md:p-8">
        <div className="max-w-5xl mx-auto">
          <div className="bg-white rounded-xl shadow-lg p-6 md:p-8 mb-6">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-indigo-100 rounded-lg">
                <SaveIcon className="w-6 h-6 text-indigo-600" />
              </div>
              <div className="flex-1">
                <h1 className="text-2xl md:text-3xl font-bold text-gray-900">Chuyển Đổi Part</h1>
                <p className="text-sm text-gray-500">Từ [[id,dx,dy],...] sang dạng object JSON</p>
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Input</label>
                <textarea
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  rows={14}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent font-mono text-xs"
                  placeholder="[[15868,-2,-27],[15871,-2,-25],[2955,0,0]]"
                />
                <div className="flex gap-2 mt-3">
                  <Button type="button" variant="outline" onClick={handleClear} className="min-w-[120px]">
                    Xóa
                  </Button>
                  <Button type="button" onClick={handleCopy} disabled={!output} className="min-w-[160px]">
                    <CheckCircleIcon className="w-5 h-5 mr-2" />
                    Copy Output
                  </Button>
                </div>
                {error && (
                  <div className="mt-3 bg-red-50 border border-red-200 rounded-lg p-3">
                    <div className="flex items-start gap-2">
                      <AlertCircleIcon className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
                      <p className="text-sm text-red-700">{error}</p>
                    </div>
                  </div>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Output</label>
                <textarea
                  value={output}
                  readOnly
                  rows={14}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 font-mono text-xs"
                  placeholder='[{"dx":0,"dy":0,"id":17}]'
                />
                <div className="mt-3 bg-indigo-50 border border-indigo-200 rounded-lg p-3">
                  <p className="text-xs text-indigo-700">
                    Output luôn theo format: <span className="font-semibold">{'{ dx, dy, id }'}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}


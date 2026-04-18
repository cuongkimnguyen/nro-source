import Link from 'next/link'
import Button from '@/components/ui/Button'
import { UploadIcon, ImageIcon } from '@/components/ui/Icons'

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center p-4">
      <div className="text-center max-w-2xl w-full">
        <h1 className="text-4xl md:text-5xl font-bold mb-4 text-gray-900">Website</h1>
        <p className="text-gray-600 mb-8">Quản lý ảnh và cải trang</p>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link href="/upload" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-blue-100 rounded-lg mb-4">
                  <UploadIcon className="w-8 h-8 text-blue-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Upload Ảnh</h2>
                <p className="text-sm text-gray-600 mb-4">Tải lên và xử lý nhiều ảnh cùng lúc</p>
                <Button size="md" className="w-full">
                  Đi đến Upload
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/add-costume" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-purple-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-purple-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Thêm Cải Trang</h2>
                <p className="text-sm text-gray-600 mb-4">Thêm cải trang mới vào hệ thống</p>
                <Button size="md" className="w-full">
                  Đi đến Thêm Cải Trang
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/add-costume-mix" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-purple-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-purple-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Cải Trang Mix</h2>
                <p className="text-sm text-gray-600 mb-4">Upload ảnh mới, chuyển part, lưu item</p>
                <Button size="md" className="w-full">
                  Đi đến Cải Trang Mix
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/add-shop-item" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-green-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-green-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Thêm Item Shop</h2>
                <p className="text-sm text-gray-600 mb-4">Thêm item vào shop</p>
                <Button size="md" className="w-full">
                  Đi đến Thêm Item Shop
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/items" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-orange-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-orange-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Danh sách Items</h2>
                <p className="text-sm text-gray-600 mb-4">Xem và tìm kiếm tất cả items</p>
                <Button size="md" className="w-full">
                  Đi đến Items
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/giftcodes" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-pink-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-pink-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Quản lý Giftcode</h2>
                <p className="text-sm text-gray-600 mb-4">Thêm, sửa và kiểm tra giftcode</p>
                <Button size="md" className="w-full">
                  Đi đến Giftcodes
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/flags" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-red-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-red-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Quản lý Flags</h2>
                <p className="text-sm text-gray-600 mb-4">Thêm, sửa, xóa và test icon flags</p>
                <Button size="md" className="w-full">
                  Đi đến Flags
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/pets" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-yellow-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-yellow-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Quản lý Pets</h2>
                <p className="text-sm text-gray-600 mb-4">Thêm, sửa, xóa pets theo sau</p>
                <Button size="md" className="w-full">
                  Đi đến Pets
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/bo-mong-no-hu" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-indigo-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-indigo-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Nổ Hũ Bò Mộng</h2>
                <p className="text-sm text-gray-600 mb-4">Settings Nổ Hũ Bò Mộng</p>
                <Button size="md" className="w-full">
                  Đi đến Settings
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/resize-images" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-teal-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-teal-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Resize Ảnh X2 → X3, X4</h2>
                <p className="text-sm text-gray-600 mb-4">Xóa x3, x4 và resize từ x2</p>
                <Button size="md" className="w-full">
                  Đi đến Resize
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/convert-part" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-indigo-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-indigo-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Chuyển Đổi Part</h2>
                <p className="text-sm text-gray-600 mb-4">[[id,dx,dy]] → JSON object</p>
                <Button size="md" className="w-full">
                  Đi đến Tool
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/add-pet-mix" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-yellow-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-yellow-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Pet Mix</h2>
                <p className="text-sm text-gray-600 mb-4">Upload ảnh mới, chuyển part, lưu pet</p>
                <Button size="md" className="w-full">
                  Đi đến Pet Mix
                </Button>
              </div>
            </div>
          </Link>

          <Link href="/add-danh-hieu-mix" className="block">
            <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center">
                <div className="p-3 bg-cyan-100 rounded-lg mb-4">
                  <ImageIcon className="w-8 h-8 text-cyan-600" />
                </div>
                <h2 className="text-xl font-semibold text-gray-900 mb-2">Danh Hiệu Mix</h2>
                <p className="text-sm text-gray-600 mb-4">Upload icon + effect, thêm danh hiệu, gen code Java</p>
                <Button size="md" className="w-full">
                  Đi đến Danh Hiệu Mix
                </Button>
              </div>
            </div>
          </Link>

        </div>
      </div>
    </div>
  )
}


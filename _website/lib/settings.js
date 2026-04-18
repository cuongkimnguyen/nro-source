const path = require('path')

module.exports = {
  STATUS_MODE_X1: false,
  STATUS_MODE_X2: true,
  STATUS_MODE_X3: true,
  STATUS_MODE_X4: true,
  
  MODE_SAVE_IMAGE: 2,
  
  paths: {
    test: {
      base: 'test-images',
      x1: path.join(process.cwd(), 'test-images', '1', 'small'),
      x2: path.join(process.cwd(), 'test-images', '2', 'small'),
      x3: path.join(process.cwd(), 'test-images', '3', 'small'),
      x4: path.join(process.cwd(), 'test-images', '4', 'small'),
      get full() {
        return path.join(process.cwd(), 'test-images')
      },
    },
    production: {
      base: 'resources/image',
      x1: path.join(process.cwd(), '..', 'resources', 'image', '1', 'small'),
      x2: path.join(process.cwd(), '..', 'resources', 'image', '2', 'small'),
      x3: path.join(process.cwd(), '..', 'resources', 'image', '3', 'small'),
      x4: path.join(process.cwd(), '..', 'resources', 'image', '4', 'small'),
      get full() {
        return path.join(process.cwd(), '..', 'resources', 'image')
      },
    },
  },
  
  imageConfig: {
    x1: { scale: 0.25, folder: '1' },
    x2: { scale: 0.5, folder: '2' },
    x3: { scale: 0.75, folder: '3' },
    x4: { scale: 1.0, folder: '4' },
  },
  
  getImagePath() {
    if (this.MODE_SAVE_IMAGE === 1) {
      return this.paths.test.full
    } else {
      return this.paths.production.full
    }
  },
  
  getImageBasePath() {
    if (this.MODE_SAVE_IMAGE === 1) {
      return this.paths.test.base
    } else {
      return this.paths.production.base
    }
  },
  
  getImagePathForSize(size) {
    const mode = this.MODE_SAVE_IMAGE === 1 ? 'test' : 'production'
    return this.paths[mode][`x${size}`]
  },
  
  getActiveSizes() {
    const sizes = []
    if (this.STATUS_MODE_X1) sizes.push({ folder: '1', scale: 0.25 })
    if (this.STATUS_MODE_X2) sizes.push({ folder: '2', scale: 0.5 })
    if (this.STATUS_MODE_X3) sizes.push({ folder: '3', scale: 0.75 })
    if (this.STATUS_MODE_X4) sizes.push({ folder: '4', scale: 1.0 })
    return sizes
  },
}


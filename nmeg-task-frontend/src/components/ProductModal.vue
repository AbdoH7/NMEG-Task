<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useMutation } from '@vue/apollo-composable'
import { CREATE_PRODUCT, UPDATE_PRODUCT } from '../graphql'
import type { Product, ProductInput, Category } from '../types'

interface Props {
  isOpen: boolean
  product?: Product | null
  categories: Category[]
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// Form data
const form = ref<ProductInput>({
  name: '',
  description: '',
  categoryId: '',
  images: []
})

// File handling
const selectedFiles = ref<Array<{ file: File; preview: string; name: string }>>([])
const existingImages = ref<string[]>([])

// Form validation
const errors = ref<Record<string, string>>({})
const loading = ref(false)

// Computed
const isEditing = computed(() => !!props.product)

// Mutations
const { mutate: createProductMutation } = useMutation(CREATE_PRODUCT)
const { mutate: updateProductMutation } = useMutation(UPDATE_PRODUCT)

// Methods
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.name?.trim()) {
    errors.value.name = 'Product name is required'
    return false
  }
  
  if (form.value.name.length < 2) {
    errors.value.name = 'Product name must be at least 2 characters'
    return false
  }
  
  if (!form.value.categoryId) {
    errors.value.categoryId = 'Please select a category'
    return false
  }
  
  return true
}

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  
  if (files) {
    Array.from(files).forEach(file => {
      if (file.type.startsWith('image/')) {
        // Check file size (10MB limit)
        if (file.size > 10 * 1024 * 1024) {
          alert(`File ${file.name} is too large. Please select a file smaller than 10MB.`)
          return
        }
        
        const reader = new FileReader()
        reader.onload = (e) => {
          const result = e.target?.result as string
          if (result && result.startsWith('data:image/')) {
            selectedFiles.value.push({
              file,
              preview: result,
              name: file.name
            })
          } else {
            alert(`Error processing image ${file.name}. Please try again.`)
          }
        }
        reader.onerror = () => {
          alert(`Error reading file ${file.name}. Please try again.`)
        }
        reader.readAsDataURL(file)
      } else {
        alert(`File ${file.name} is not an image. Please select only image files.`)
      }
    })
  }
  
  // Clear the input so the same file can be selected again
  target.value = ''
}

const removeFile = (index: number) => {
  selectedFiles.value.splice(index, 1)
}

const handleDrop = (event: DragEvent) => {
  const files = event.dataTransfer?.files
  if (files) {
    // Create a fake input event to reuse the existing handler
    const fakeEvent = {
      target: { files } as HTMLInputElement
    } as Event
    handleFileUpload(fakeEvent)
  }
}

const removeExistingImage = (index: number) => {
  existingImages.value.splice(index, 1)
}

const detectImageFormat = (base64: string): string => {
  // Check base64 header to determine image format
  if (base64.startsWith('/9j/') || base64.startsWith('iVBORw0KGgo')) {
    return 'image/png'
  } else if (base64.startsWith('R0lGOD')) {
    return 'image/gif'
  } else if (base64.startsWith('UklGR')) {
    return 'image/webp'
  } else if (base64.startsWith('iVBORw0KGgo')) {
    return 'image/png'
  }
  // Default to PNG if format can't be determined
  return 'image/png'
}

const handleExistingImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
  // Show fallback icon
  const parent = img.parentElement
  if (parent) {
    parent.innerHTML = '<div class="w-full h-20 bg-gray-200 rounded border flex items-center justify-center text-gray-400">📷</div>'
  }
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    // Convert files to base64 strings
    const newImages = selectedFiles.value.map(fileData => fileData.preview)
    const allImages = [...existingImages.value, ...newImages]
    
    const input = {
      name: form.value.name.trim(),
      description: form.value.description?.trim() || '',
      categoryId: form.value.categoryId,
      images: allImages
    }
    
    if (isEditing.value && props.product) {
      await updateProductMutation({
        id: props.product.id,
        input
      })
    } else {
      await createProductMutation({ input })
    }
    
    emit('success')
    closeModal()
  } catch (error) {
    // You could add error handling here (toast notification, etc.)
  } finally {
    loading.value = false
  }
}

const closeModal = () => {
  emit('close')
}

const resetForm = () => {
  form.value = {
    name: '',
    description: '',
    categoryId: '',
    images: []
  }
  selectedFiles.value = []
  existingImages.value = []
  errors.value = {}
}

watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = {
      name: newProduct.name,
      description: newProduct.description || '',
      categoryId: newProduct.categoryId,
      images: newProduct.images || []
    }
    
    // Convert raw base64 to data URLs for display
    const convertedImages = (newProduct.images || []).map(img => {
      if (img.startsWith('data:image/')) {
        return img
      }
      // Detect format and create proper data URL
      const format = detectImageFormat(img)
      return `data:${format};base64,${img}`
    })
    
    existingImages.value = convertedImages
  } else {
    resetForm()
  }
}, { immediate: true })

 watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold text-gray-800">
          {{ isEditing ? 'Edit Product' : 'Add Product' }}
        </h2>
        <button
          @click="closeModal"
          class="text-gray-400 hover:text-gray-600 transition-colors"
        >
          ✕
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- Product Name -->
        <div>
          <label for="productName" class="block text-sm font-medium text-gray-700 mb-1">
            Product Name *
          </label>
          <input
            id="productName"
            v-model="form.name"
            type="text"
            required
            class="input-field w-full"
            :class="{ 'border-red-500': errors.name }"
            placeholder="Enter product name"
          />
          <p v-if="errors.name" class="text-red-500 text-sm mt-1">{{ errors.name }}</p>
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            id="description"
            v-model="form.description"
            rows="3"
            class="input-field w-full resize-none"
            placeholder="Enter product description"
          />
        </div>

        <!-- Category -->
        <div>
          <label for="category" class="block text-sm font-medium text-gray-700 mb-1">
            Category *
          </label>
          <select
            id="category"
            v-model="form.categoryId"
            required
            class="input-field w-full"
            :class="{ 'border-red-500': errors.categoryId }"
          >
            <option value="">Select a category</option>
            <option 
              v-for="category in categories" 
              :key="category.id" 
              :value="category.id"
            >
              {{ category.name }}
            </option>
          </select>
          <p v-if="errors.categoryId" class="text-red-500 text-sm mt-1">{{ errors.categoryId }}</p>
        </div>

        <!-- Images Upload -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Images
          </label>
          <div class="border-2 border-dashed border-gray-300 rounded-lg p-4">
            <input
              ref="fileInput"
              type="file"
              multiple
              accept="image/*"
              @change="handleFileUpload"
              class="hidden"
            />
            
            <!-- Upload Area -->
            <div 
              @click="$refs.fileInput.click()"
              @dragover.prevent
              @dragenter.prevent
              @drop.prevent="handleDrop"
              class="text-center cursor-pointer hover:bg-gray-50 p-4 rounded border-2 border-dashed border-gray-300 hover:border-gray-400 transition-colors"
            >
              <div class="text-gray-400 mb-2">
                📷
              </div>
              <p class="text-sm text-gray-600">
                Click to upload images or drag and drop
              </p>
              <p class="text-xs text-gray-500 mt-1">
                PNG, JPG, GIF up to 10MB each
              </p>
            </div>

            <!-- Selected Files Preview -->
            <div v-if="selectedFiles.length > 0" class="mt-4">
              <h4 class="text-sm font-medium text-gray-700 mb-2">Selected Images:</h4>
              <div class="grid grid-cols-4 gap-2">
                <div 
                  v-for="(file, index) in selectedFiles" 
                  :key="index"
                  class="relative group"
                >
                  <img 
                    :src="file.preview" 
                    :alt="file.name"
                    class="w-full h-20 object-cover rounded border"
                  />
                  <button
                    @click="removeFile(index)"
                    class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    ✕
                  </button>
                  <p class="text-xs text-gray-600 truncate mt-1">{{ file.name }}</p>
                </div>
              </div>
            </div>

            <div v-if="existingImages.length > 0" class="mt-4">
              <h4 class="text-sm font-medium text-gray-700 mb-2">Current Images:</h4>
              <div class="grid grid-cols-4 gap-2">
                <div 
                  v-for="(image, index) in existingImages" 
                  :key="index"
                  class="relative group"
                >
                  <img 
                    :src="image" 
                    :alt="`Existing image ${index + 1}`"
                    class="w-full h-20 object-cover rounded border"
                    @error="handleExistingImageError"
                  />
                  <button
                    @click="removeExistingImage(index)"
                    class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    ✕
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex items-center justify-end space-x-3 pt-4">
          <button
            type="button"
            @click="closeModal"
            class="px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="loading"
            class="btn-primary"
            :class="{ 'opacity-50 cursor-not-allowed': loading }"
          >
            <span v-if="loading" class="animate-spin mr-2">⏳</span>
            {{ isEditing ? 'Update' : 'Save' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

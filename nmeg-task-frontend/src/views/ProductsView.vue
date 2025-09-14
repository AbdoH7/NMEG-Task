<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuery, useMutation } from '@vue/apollo-composable'
import { 
  GET_PRODUCTS, 
  SEARCH_PRODUCTS_BY_NAME, 
  DELETE_PRODUCT,
  GET_CATEGORIES
} from '../graphql'
import ProductModal from '../components/ProductModal.vue'
import type { Product, Category } from '../types'

const searchQuery = ref('')
const isSearching = ref(false)

// Modal state
const isModalOpen = ref(false)
const selectedProduct = ref<Product | null>(null)

// Queries
const { result: productsResult, loading, refetch } = useQuery(GET_PRODUCTS)
const { result: searchResult } = useQuery(SEARCH_PRODUCTS_BY_NAME, () => ({
  name: searchQuery.value
}), () => searchQuery.value.length > 0)
const { result: categoriesResult } = useQuery(GET_CATEGORIES)

// Mutations
const { mutate: deleteProductMutation } = useMutation(DELETE_PRODUCT)

// Computed
const products = computed(() => productsResult.value?.products || [])
const searchProducts = computed(() => searchResult.value?.searchProductsByName || [])
const categories = computed(() => categoriesResult.value?.categories || [])
const filteredProducts = computed(() => {
  if (isSearching.value && searchQuery.value.length > 0) {
    return searchProducts.value
  }
  return products.value
})

// Methods
const handleSearch = () => {
  isSearching.value = searchQuery.value.length > 0
}

const isProductValid = (product: Product) => {
  // Product is valid if it has images
  return getImageCount(product) > 0
}

const getImageCount = (product: Product) => {
  if (!product.images || !Array.isArray(product.images)) return 0
  return product.images.length
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

const getImageUrls = (product: Product) => {
  if (!product.images || !Array.isArray(product.images)) return []
  
  // Convert raw base64 to data URLs
  const dataUrls = product.images.map(img => {
    // Check if it's already a data URL
    if (img.startsWith('data:image/')) {
      return img
    }
    // Detect format and create proper data URL
    const format = detectImageFormat(img)
    return `data:${format};base64,${img}`
  })
  
  return dataUrls
}

const openAddModal = () => {
  selectedProduct.value = null
  isModalOpen.value = true
}

const openEditModal = (product: Product) => {
  selectedProduct.value = product
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  selectedProduct.value = null
}

const handleModalSuccess = () => {
  refetch() // Refresh the products list
}

const deleteProduct = async (id: string) => {
  if (confirm('Are you sure you want to delete this product?')) {
    try {
      await deleteProductMutation({ id })
      refetch()
    } catch (error) {
      // Handle error appropriately
    }
  }
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
  // Show fallback icon
  const parent = img.parentElement
  if (parent) {
    parent.innerHTML = '📷'
  }
}

onMounted(() => {
  refetch()
})
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-800">PRODUCTS</h1>
    </div>
    
    <!-- Search and Add Section -->
    <div class="flex items-center justify-between">
      <!-- Search Bar -->
      <div class="relative">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search"
          class="px-4 py-2 w-80 bg-gray-100 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          @input="handleSearch"
        />
      </div>
      
      <!-- Add Button -->
      <button
        @click="openAddModal"
        class="btn-primary"
      >
        Add Product
      </button>
    </div>
    
    <!-- Products Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="table-header">STATUS</th>
            <th class="table-header">NAME</th>
            <th class="table-header">DESCRIPTION</th>
            <th class="table-header">CATEGORY</th>
            <th class="table-header">IMAGES</th>
            <th class="table-header">ACTIONS</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="loading" class="animate-pulse">
            <td class="table-cell" colspan="6">
              <div class="h-4 bg-gray-200 rounded"></div>
            </td>
          </tr>
          <tr v-else-if="filteredProducts.length === 0">
            <td class="table-cell text-center text-gray-500" colspan="6">
              No products found
            </td>
          </tr>
          <tr v-else v-for="product in filteredProducts" :key="product.id">
            <td class="table-cell">
              <div class="flex items-center">
                <div 
                  class="w-6 h-6 rounded-full flex items-center justify-center"
                  :class="isProductValid(product) ? 'bg-green-500' : 'bg-gray-300'"
                >
                  <span 
                    v-if="isProductValid(product)"
                    class="text-white text-xs"
                  >✓</span>
                </div>
              </div>
            </td>
            <td class="table-cell font-medium">{{ product.name }}</td>
            <td class="table-cell">
              <div class="max-w-xs truncate" :title="product.description">
                {{ product.description || 'No description' }}
              </div>
            </td>
            <td class="table-cell">
              {{ product.category?.name || 'No category' }}
            </td>
            <td class="table-cell">
              <div class="flex items-center space-x-1">
                <span class="text-sm text-gray-600">
                  {{ getImageCount(product) }} image(s)
                </span>
                <div v-if="getImageCount(product) > 0" class="flex space-x-1">
                  <div 
                    v-for="(image, index) in getImageUrls(product).slice(0, 3)" 
                    :key="index"
                    class="w-6 h-6 bg-gray-200 rounded border flex items-center justify-center text-xs overflow-hidden"
                  >
                    <img 
                      :src="image" 
                      :alt="`Product image ${index + 1}`"
                      class="w-full h-full object-cover"
                      @error="handleImageError"
                    />
                  </div>
                  <span v-if="getImageCount(product) > 3" class="text-xs text-gray-500">
                    +{{ getImageCount(product) - 3 }}
                  </span>
                </div>
              </div>
            </td>
            <td class="table-cell">
              <div class="flex items-center space-x-2">
                <button
                  @click="openEditModal(product)"
                  class="p-2 text-gray-600 hover:text-blue-600 transition-colors"
                >
                  ✏️
                </button>
                <button
                  @click="deleteProduct(product.id)"
                  class="p-2 text-gray-600 hover:text-red-600 transition-colors"
                >
                  🗑️
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Product Modal -->
    <ProductModal
      :is-open="isModalOpen"
      :product="selectedProduct"
      :categories="categories"
      @close="closeModal"
      @success="handleModalSuccess"
    />
  </div>
</template>

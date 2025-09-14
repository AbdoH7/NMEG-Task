<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useQuery, useMutation } from '@vue/apollo-composable'
import { 
  GET_CATEGORIES, 
  SEARCH_CATEGORIES, 
  DELETE_CATEGORY 
} from '../graphql'
import CategoryModal from '../components/CategoryModal.vue'
import type { Category } from '../types'

const searchQuery = ref('')
const isSearching = ref(false)

// Modal state
const isModalOpen = ref(false)
const selectedCategory = ref<Category | null>(null)

// Queries
const { result: categoriesResult, loading, refetch } = useQuery(GET_CATEGORIES)
const { result: searchResult } = useQuery(SEARCH_CATEGORIES, () => ({
  name: searchQuery.value
}), () => searchQuery.value.length > 0)

// Mutations
const { mutate: deleteCategoryMutation } = useMutation(DELETE_CATEGORY)

// Computed
const categories = computed(() => categoriesResult.value?.categories || [])
const searchCategories = computed(() => searchResult.value?.searchCategories || [])
const filteredCategories = computed(() => {
  if (isSearching.value && searchQuery.value.length > 0) {
    return searchCategories.value
  }
  return categories.value
})

// Methods
const handleSearch = () => {
  isSearching.value = searchQuery.value.length > 0
}

const isCategoryValid = (category: Category) => {
  const hasProducts = category.products && category.products.length > 0
  const now = new Date()
  const validFrom = category.validFrom ? new Date(category.validFrom) : null
  const validTo = category.validTo ? new Date(category.validTo) : null
  
  const isDateValid = (!validFrom || validFrom <= now) && (!validTo || validTo >= now)
  
  return hasProducts && isDateValid
}

const formatDate = (dateString?: string) => {
  if (!dateString) return 'No value'
  return new Date(dateString).toLocaleString()
}

const openAddModal = () => {
  selectedCategory.value = null
  isModalOpen.value = true
}

const openEditModal = (category: Category) => {
  selectedCategory.value = category
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  selectedCategory.value = null
}

const handleModalSuccess = () => {
  refetch() // Refresh the categories list
}

const deleteCategory = async (id: string) => {
  if (confirm('Are you sure you want to delete this category?')) {
    try {
      await deleteCategoryMutation({ id })
      refetch()
    } catch (error) {
      console.error('Error deleting category:', error)
    }
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
      <h1 class="text-2xl font-bold text-gray-800">CATEGORIES</h1>
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
        Add Category
      </button>
    </div>
    
    <!-- Categories Table -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="table-header">STATUS</th>
            <th class="table-header">NAME</th>
            <th class="table-header">VALID FROM</th>
            <th class="table-header">VALID TO</th>
            <th class="table-header">ACTIONS</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="loading" class="animate-pulse">
            <td class="table-cell" colspan="5">
              <div class="h-4 bg-gray-200 rounded"></div>
            </td>
          </tr>
          <tr v-else-if="filteredCategories.length === 0">
            <td class="table-cell text-center text-gray-500" colspan="5">
              No categories found
            </td>
          </tr>
          <tr v-else v-for="category in filteredCategories" :key="category.id">
            <td class="table-cell">
              <div class="flex items-center">
                <div 
                  class="w-6 h-6 rounded-full flex items-center justify-center"
                  :class="isCategoryValid(category) ? 'bg-green-500' : 'bg-gray-300'"
                >
                  <span 
                    v-if="isCategoryValid(category)"
                    class="text-white text-xs"
                  >✓</span>
                </div>
              </div>
            </td>
            <td class="table-cell font-medium">{{ category.name }}</td>
            <td class="table-cell">
              {{ formatDate(category.validFrom) }}
            </td>
            <td class="table-cell">
              {{ category.validTo ? formatDate(category.validTo) : 'No value' }}
            </td>
            <td class="table-cell">
              <div class="flex items-center space-x-2">
                <button
                  @click="openEditModal(category)"
                  class="p-2 text-gray-600 hover:text-blue-600 transition-colors"
                >
                  ✏️
                </button>
                <button
                  @click="deleteCategory(category.id)"
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

    <!-- Category Modal -->
    <CategoryModal
      :is-open="isModalOpen"
      :category="selectedCategory"
      @close="closeModal"
      @success="handleModalSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useMutation } from '@vue/apollo-composable'
import { CREATE_CATEGORY, UPDATE_CATEGORY } from '../graphql'
import type { Category, CategoryInput } from '../types'

interface Props {
  isOpen: boolean
  category?: Category | null
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// Form data
const form = ref<CategoryInput>({
  name: '',
  validFrom: null,
  validTo: null
})

// Form validation
const errors = ref<Record<string, string>>({})
const loading = ref(false)

// Computed
const isEditing = computed(() => !!props.category)

// Mutations
const { mutate: createCategoryMutation } = useMutation(CREATE_CATEGORY)
const { mutate: updateCategoryMutation } = useMutation(UPDATE_CATEGORY)

// Methods
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.name?.trim()) {
    errors.value.name = 'Category name is required'
    return false
  }
  
  if (form.value.name.length < 2) {
    errors.value.name = 'Category name must be at least 2 characters'
    return false
  }
  
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    const input = {
      name: form.value.name.trim(),
      validFrom: form.value.validFrom || null,
      validTo: form.value.validTo || null
    }
    
    if (isEditing.value && props.category) {
      await updateCategoryMutation({
        id: props.category.id,
        input
      })
    } else {
      await createCategoryMutation({ input })
    }
    
    emit('success')
    closeModal()
  } catch (error) {
    console.error('Error saving category:', error)
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
    validFrom: null,
    validTo: null
  }
  errors.value = {}
}

// Watch for category changes to populate form
watch(() => props.category, (newCategory) => {
  if (newCategory) {
    form.value = {
      name: newCategory.name,
      validFrom: newCategory.validFrom ? new Date(newCategory.validFrom).toISOString().slice(0, 16) : null,
      validTo: newCategory.validTo ? new Date(newCategory.validTo).toISOString().slice(0, 16) : null
    }
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
    <div class="bg-white rounded-lg p-6 w-96 max-w-md mx-4">
      <!-- Header -->
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold text-gray-800">
          {{ isEditing ? 'Edit Category' : 'Add Category' }}
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
        <!-- Category Name -->
        <div>
          <label for="categoryName" class="block text-sm font-medium text-gray-700 mb-1">
            Category Name *
          </label>
          <input
            id="categoryName"
            v-model="form.name"
            type="text"
            required
            class="input-field w-full"
            :class="{ 'border-red-500': errors.name }"
            placeholder="Enter category name"
          />
          <p v-if="errors.name" class="text-red-500 text-sm mt-1">{{ errors.name }}</p>
        </div>

        <!-- Valid From -->
        <div>
          <label for="validFrom" class="block text-sm font-medium text-gray-700 mb-1">
            Valid From
          </label>
          <input
            id="validFrom"
            v-model="form.validFrom"
            type="datetime-local"
            class="input-field w-full"
          />
        </div>

        <!-- Valid To -->
        <div>
          <label for="validTo" class="block text-sm font-medium text-gray-700 mb-1">
            Valid To
          </label>
          <input
            id="validTo"
            v-model="form.validTo"
            type="datetime-local"
            class="input-field w-full"
          />
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

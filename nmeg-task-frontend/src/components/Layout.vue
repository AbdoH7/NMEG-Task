<template>
  <div class="min-h-screen bg-white">
    <!-- Sidebar -->
    <div class="fixed inset-y-0 left-0 w-64 bg-gray-50 border-r border-gray-200">
      <div class="flex flex-col h-full">
        <!-- Logo/Header -->
        <div class="flex items-center justify-center h-16 px-4 border-b border-gray-200">
          <h1 class="text-xl font-semibold text-gray-800">NMEG Task</h1>
        </div>
        
        <!-- Navigation -->
        <nav class="flex-1 px-4 py-6 space-y-2">
          <router-link
            to="/categories"
            class="flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors duration-200"
            :class="[
              $route.name === 'categories'
                ? 'bg-blue-100 text-blue-700 border border-blue-200'
                : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
            ]"
          >
            📁 Categories
          </router-link>

          <router-link
            to="/products"
            class="flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors duration-200"
            :class="[
              $route.name === 'products'
                ? 'bg-blue-100 text-blue-700 border border-blue-200'
                : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
            ]"
          >
            📦 Products
          </router-link>
        </nav>
      </div>
    </div>
    
    <!-- Main Content -->
    <div class="ml-64">
      <main class="p-8">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
// Layout component with navigation
</script>

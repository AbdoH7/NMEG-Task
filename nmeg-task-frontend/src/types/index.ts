export interface Category {
  id: string
  name: string
  validFrom?: string
  validTo?: string
  products?: Product[]
}

export interface CategoryInput {
  name: string
  validFrom?: string
  validTo?: string
}

export interface Product {
  id: string
  name: string
  description?: string
  categoryId: string
  category?: Category
  images?: string[]
}

export interface ProductInput {
  name: string
  description?: string
  categoryId: string
  images?: string[]
}

// GraphQL response types
export interface CreateCategoryData {
  createCategory: Category
}

export interface UpdateCategoryData {
  updateCategory: Category
}

export interface DeleteCategoryData {
  deleteCategory: boolean
}

export interface CreateProductData {
  createProduct: Product
}

export interface UpdateProductData {
  updateProduct: Product
}

export interface DeleteProductData {
  deleteProduct: boolean
}

export interface CategoriesData {
  categories: Category[]
}

export interface ProductsData {
  products: Product[]
}

export interface SearchCategoriesData {
  searchCategories: Category[]
}

export interface SearchProductsByNameData {
  searchProductsByName: Product[]
}

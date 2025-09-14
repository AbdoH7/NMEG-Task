import { gql } from '@apollo/client/core'

export const GET_CATEGORIES = gql`
  query GetCategories {
    categories {
      id
      name
      validFrom
      validTo
      products {
        id
      }
    }
  }
`

export const GET_CATEGORY = gql`
  query GetCategory($id: ID!) {
    category(id: $id) {
      id
      name
      validFrom
      validTo
      products {
        id
        name
        description
      }
    }
  }
`

export const SEARCH_CATEGORIES = gql`
  query SearchCategories($name: String!) {
    searchCategories(name: $name) {
      id
      name
      validFrom
      validTo
      products {
        id
      }
    }
  }
`

export const GET_PRODUCTS = gql`
  query GetProducts {
    products {
      id
      name
      description
      categoryId
      category {
        id
        name
      }
      images
    }
  }
`

export const GET_PRODUCT = gql`
  query GetProduct($id: ID!) {
    product(id: $id) {
      id
      name
      description
      categoryId
      category {
        id
        name
      }
      images
    }
  }
`

export const SEARCH_PRODUCTS_BY_NAME = gql`
  query SearchProductsByName($name: String!) {
    searchProductsByName(name: $name) {
      id
      name
      description
      categoryId
      category {
        id
        name
      }
      images
    }
  }
`

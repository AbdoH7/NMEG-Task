package org.example.nmegtaskbackend.service;

import org.example.nmegtaskbackend.dto.ProductInput;
import org.example.nmegtaskbackend.entity.Product;
import org.example.nmegtaskbackend.entity.ProductImage;
import org.example.nmegtaskbackend.exception.ResourceNotFoundException;
import org.example.nmegtaskbackend.exception.ValidationException;
import org.example.nmegtaskbackend.repository.ProductImageRepository;
import org.example.nmegtaskbackend.repository.ProductRepository;
import org.example.nmegtaskbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
    }

    public Product createProduct(ProductInput productInput) {
        // Validate input
        validateProductInput(productInput);

        if (!categoryRepository.existsById(productInput.getCategoryId())) {
            throw new ResourceNotFoundException("Category", productInput.getCategoryId());
        }
        
        Product product = new Product();
        product.setName(productInput.getName());
        product.setDescription(productInput.getDescription());
        product.setCategoryId(productInput.getCategoryId());

        Product savedProduct =  productRepository.save(product);

        if (productInput.getImages() != null && !productInput.getImages().isEmpty()) {
            saveProductImages(savedProduct.getId(), productInput.getImages());
        }

        return savedProduct;
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProductsByCategoryAndName(Long categoryId, String name) {
        return productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name);
    }
    
    public Product updateProduct(Long id, ProductInput productInput) {
        validateProductInput(productInput);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        
        // Check if category exists
        if (!categoryRepository.existsById(productInput.getCategoryId())) {
            throw new ResourceNotFoundException("Category", productInput.getCategoryId());
        }

        
        existingProduct.setName(productInput.getName());
        existingProduct.setDescription(productInput.getDescription());
        existingProduct.setCategoryId(productInput.getCategoryId());

        if (productInput.getImages() != null) {
            // Delete existing images
            // productImageRepository.deleteByProductId(id);
            // Add new images
            saveProductImages(id, productInput.getImages());
        }
        
        return productRepository.save(existingProduct);
    }
    
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
    // Validate product input
    private void validateProductInput(ProductInput productInput) {
        if (productInput.getName() == null || productInput.getName().trim().isEmpty()) {
            throw new ValidationException("Product name cannot be null or empty");
        }
        
        if (productInput.getCategoryId() == null) {
            throw new ValidationException("Category ID cannot be null");
        }
    }

    private void saveProductImages(Long productId, List<String> base64Images) {
        for (int i = 0; i < base64Images.size(); i++) {
            String base64String = base64Images.get(i);
            
            // Clean the base64 string (remove data URL prefix if present)
            if (base64String.startsWith("data:")) {
                base64String = base64String.substring(base64String.indexOf(",") + 1);
            }
            
            try {
                byte[] imageData = Base64.getDecoder().decode(base64String);
                ProductImage productImage = new ProductImage(imageData, productId, i);
                productImageRepository.save(productImage);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid base64 image data at index " + i + ": " + e.getMessage());
            }
        }
    }
}

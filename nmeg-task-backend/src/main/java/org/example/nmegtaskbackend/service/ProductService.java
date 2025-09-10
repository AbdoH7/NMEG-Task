package org.example.nmegtaskbackend.service;

import org.example.nmegtaskbackend.dto.ProductInput;
import org.example.nmegtaskbackend.entity.Product;
import org.example.nmegtaskbackend.repository.ProductRepository;
import org.example.nmegtaskbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(ProductInput productInput) {
        // Validate input
        validateProductInput(productInput);

        if (!categoryRepository.existsById(productInput.getCategoryId())) {
            throw new IllegalArgumentException("Category with id " + productInput.getCategoryId() + " not found");
        }

        
        Product product = new Product();
        product.setName(productInput.getName());
        product.setDescription(productInput.getDescription());
        product.setCategoryId(productInput.getCategoryId());
        
        return productRepository.save(product);
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
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + id + " not found"));
        
        // Check if category exists
        if (!categoryRepository.existsById(productInput.getCategoryId())) {
            throw new IllegalArgumentException("Category with id " + productInput.getCategoryId() + " not found");
        }

        
        existingProduct.setName(productInput.getName());
        existingProduct.setDescription(productInput.getDescription());
        existingProduct.setCategoryId(productInput.getCategoryId());
        
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
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        
        if (productInput.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
    }
}

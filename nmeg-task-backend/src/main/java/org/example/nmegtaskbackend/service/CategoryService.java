package org.example.nmegtaskbackend.service;

import org.example.nmegtaskbackend.dto.CategoryInput;
import org.example.nmegtaskbackend.entity.Category;
import org.example.nmegtaskbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryInput categoryInput) {
        // Validate input
        validateCategoryInput(categoryInput);

        if (categoryRepository.existsByName(categoryInput.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryInput.getName() + "' already exists");
        }
        
        Category category = new Category();
        category.setName(categoryInput.getName());
        category.setValidFrom(categoryInput.getValidFrom() != null ? categoryInput.getValidFrom() : LocalDateTime.now());
        category.setValidTo(categoryInput.getValidTo());
        
        return categoryRepository.save(category);
    }
    
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByValidToIsNullOrValidToAfter(LocalDateTime.now());
    }
    
    // Get categories valid at a specific time
    @Transactional(readOnly = true)
    public List<Category> getCategoriesValidAt(LocalDateTime dateTime) {
        return categoryRepository.findCategoriesValidAt(dateTime);
    }
    
    // Search categories by name
    @Transactional(readOnly = true)
    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Update category
    public Category updateCategory(Long id, CategoryInput categoryInput) {
        // Validate input
        validateCategoryInput(categoryInput);
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with id " + id + " not found"));
        
        // Check if new name conflicts with existing categories (excluding current)
        if (!existingCategory.getName().equals(categoryInput.getName()) && 
            categoryRepository.existsByNameAndIdNot(categoryInput.getName(), id)) {
            throw new IllegalArgumentException("Category with name '" + categoryInput.getName() + "' already exists");
        }
        
        existingCategory.setName(categoryInput.getName());
        existingCategory.setValidFrom(categoryInput.getValidFrom() != null ? categoryInput.getValidFrom() : existingCategory.getValidFrom());
        existingCategory.setValidTo(categoryInput.getValidTo());
        
        return categoryRepository.save(existingCategory);
    }
    
    // Delete category
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
    // Validate category input
    private void validateCategoryInput(CategoryInput categoryInput) {
        if (categoryInput.getName() == null || categoryInput.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        
        if (categoryInput.getValidFrom() != null && categoryInput.getValidTo() != null) {
            if (categoryInput.getValidFrom().isAfter(categoryInput.getValidTo())) {
                throw new IllegalArgumentException("Valid from date cannot be after valid to date");
            }
        }
    }
}

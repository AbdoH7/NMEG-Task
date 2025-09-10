package org.example.nmegtaskbackend.controller;

import org.example.nmegtaskbackend.dto.CategoryInput;
import org.example.nmegtaskbackend.entity.Category;
import org.example.nmegtaskbackend.service.CategoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }
    
    @QueryMapping
    public Optional<Category> category(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }
    
    @QueryMapping
    public List<Category> activeCategories() {
        return categoryService.getActiveCategories();
    }
    
    @QueryMapping
    public List<Category> categoriesValidAt(@Argument LocalDateTime dateTime) {
        return categoryService.getCategoriesValidAt(dateTime);
    }
    
    @QueryMapping
    public List<Category> searchCategories(@Argument String name) {
        return categoryService.searchCategoriesByName(name);
    }
    
    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        return categoryService.createCategory(input);
    }
    
    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
        return categoryService.updateCategory(id, input);
    }
    
    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryService.deleteCategory(id);
    }
}

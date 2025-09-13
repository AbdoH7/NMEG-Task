package org.example.nmegtaskbackend.controller;

import org.example.nmegtaskbackend.dto.ProductInput;
import org.example.nmegtaskbackend.entity.Product;
import org.example.nmegtaskbackend.entity.ProductImage;
import org.example.nmegtaskbackend.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }
    
    @QueryMapping
    public Optional<Product> product(@Argument Long id) {
        return productService.getProductById(id);
    }
    
    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
    
    @QueryMapping
    public List<Product> searchProductsByName(@Argument String name) {
        return productService.searchProductsByName(name);
    }
    
    @QueryMapping
    public List<Product> searchProductsByCategoryAndName(@Argument Long categoryId, @Argument String name) {
        return productService.searchProductsByCategoryAndName(categoryId, name);
    }
    
    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        return productService.createProduct(input);
    }
    
    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
        return productService.updateProduct(id, input);
    }
    
    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return productService.deleteProduct(id);
    }
    
    @SchemaMapping(typeName = "Product", field = "images")
    public List<String> images(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return List.of();
        }
        
        return product.getImages().stream()
                .map(productImage -> Base64.getEncoder().encodeToString(productImage.getImage()))
                .collect(Collectors.toList());
    }
}

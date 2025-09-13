package org.example.nmegtaskbackend.dto;

import java.util.List;

public class ProductInput {
    
    private String name;
    private String description;
    private Long categoryId;
    private List<String> images;
    
    public ProductInput() {}

    public ProductInput(String name, String description, Long categoryId, List<String> images) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.images = images;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

package org.example.nmegtaskbackend.dto;

public class ProductInput {
    
    private String name;
    private String description;
    private Long categoryId;
    
    public ProductInput() {}
    
    public ProductInput(String name, String description, Long categoryId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
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

package org.example.nmegtaskbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CategoryInput {
    
    private String name;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime validFrom;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime validTo;
    
    // Default constructor
    public CategoryInput() {}
    
    // Constructor with all fields
    public CategoryInput(String name, LocalDateTime validFrom, LocalDateTime validTo) {
        this.name = name;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDateTime getValidTo() {
        return validTo;
    }
    
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}

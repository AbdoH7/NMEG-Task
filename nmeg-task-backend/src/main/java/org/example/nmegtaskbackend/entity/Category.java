package org.example.nmegtaskbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_to")
    private LocalDateTime validTo;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
    
    public Category() {}
    
    public Category(String name, LocalDateTime validFrom, LocalDateTime validTo) {
        this.name = name;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

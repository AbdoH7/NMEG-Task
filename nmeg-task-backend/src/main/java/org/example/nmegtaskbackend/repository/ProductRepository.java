package org.example.nmegtaskbackend.repository;

import org.example.nmegtaskbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByNameContainingIgnoreCase(String namePattern);

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Product> findByCategoryIdAndNameContainingIgnoreCase(@Param("categoryId") Long categoryId, @Param("namePattern") String namePattern);
}

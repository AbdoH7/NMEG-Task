package org.example.nmegtaskbackend.repository;

import org.example.nmegtaskbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find active categories (valid_to is null or in the future)
//    @Query("SELECT c FROM Category c WHERE c.validTo IS NULL OR c.validTo > :currentTime")
//    List<Category> findActiveCategories(@Param("currentTime") LocalDateTime currentTime);

    List<Category> findByValidToIsNullOrValidToAfter(LocalDateTime currentTime);
    
    // Find categories valid at a specific time
    @Query("SELECT c FROM Category c WHERE c.validFrom <= :dateTime AND (c.validTo IS NULL OR c.validTo > :dateTime)")
    List<Category> findCategoriesValidAt(@Param("dateTime") LocalDateTime dateTime);
    
    List<Category> findByNameContainingIgnoreCase(String namePattern);

    // Check if category name exists (excluding current category for updates)
    boolean existsByNameAndIdNot(String name, Long id);
    
    boolean existsByName(String name);
}

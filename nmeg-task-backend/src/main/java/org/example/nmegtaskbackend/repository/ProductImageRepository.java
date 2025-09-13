package org.example.nmegtaskbackend.repository;

import org.example.nmegtaskbackend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>
{
    List<ProductImage> findByProductIdOrderByImageOrderAsc(Long productId);
    void deleteByProductId(Long productId);
}

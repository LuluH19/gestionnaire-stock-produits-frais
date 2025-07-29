package com.stock.manager.repository;

import com.stock.manager.entity.Product;
import com.stock.manager.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByExpirationDateBetween(LocalDate start, LocalDate end);
}
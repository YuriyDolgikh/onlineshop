package org.onlineshop.repository;

import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByNameContainingIgnoreCase(String partOfName, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice, Pageable pageable);

    Page<Product> findByDiscountPriceGreaterThan(BigDecimal discountPrice, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);
}
package org.onlineshop.repository;

import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findById(Integer id);

    List<Product> findByCategory(Category category);

    List<Product> findByName(Integer id);

    List<Product> findByNameAndCategory(String name, Category category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal priceFrom, BigDecimal priceTo);

}

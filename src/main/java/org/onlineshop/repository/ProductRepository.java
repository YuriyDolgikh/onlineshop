package org.onlineshop.repository;

import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(Category category);

    List<Product> findByName(String name);

    List<Product> findByNameOrderByName(String name, Sort.Direction sort);

    List<Product> findByNameOrderByNameAsc(String name);
    List<Product> findByNameOrderByNameDesc(String name);

    List<Product> findByNameAndCategory(String name, Category category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal priceFrom, BigDecimal priceTo);

}

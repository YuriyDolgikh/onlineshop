package org.onlineshop.repository;

import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String partOfName, Sort sort);

    List<Product> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice, Sort sort);

    List<Product> findByDiscountPriceGreaterThan(BigDecimal discountPrice, Sort sort);

    List<Product> findByCategory(Category category, Sort sort);
}

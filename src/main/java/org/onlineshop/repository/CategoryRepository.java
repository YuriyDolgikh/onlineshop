package org.onlineshop.repository;

import org.onlineshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryId(Integer categoryId);

    Optional<Category> findByCategoryName(String name);

    boolean existsByCategoryName(String categoryName);
}

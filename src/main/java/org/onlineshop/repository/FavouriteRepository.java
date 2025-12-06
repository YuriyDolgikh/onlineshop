package org.onlineshop.repository;

import org.onlineshop.entity.Favourite;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    Page<Favourite> findByUser(User user, Pageable pageable);

    Optional<Favourite> findByUserAndProduct(User user, Product product);

}

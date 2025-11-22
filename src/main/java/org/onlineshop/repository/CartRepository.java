package org.onlineshop.repository;

import org.onlineshop.entity.Cart;
import org.onlineshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);

//    boolean existsByUser(User user);

}

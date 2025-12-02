package org.onlineshop.repository;

import org.onlineshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

//    List<CartItem> findByCart(Cart cart);
//    Optional<CartItem> findByCartAndProduct(Cart cart, CartItem cartItem);
}

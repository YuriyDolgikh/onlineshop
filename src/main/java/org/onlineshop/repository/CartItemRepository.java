package org.onlineshop.repository;

import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    List<CartItem> findByCart(Cart cart);//показать содержимое корзие
    Optional<CartItem> findByCartAndProduct(Cart cart, CartItem cartItem);//проверить если уже такой товар в корзине
}

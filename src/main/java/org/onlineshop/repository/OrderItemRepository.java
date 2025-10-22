package org.onlineshop.repository;

import org.onlineshop.entity.Cart;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByOrderId(Integer  orderId);//
    List<OrderItem> findByOrder(Order order);
}

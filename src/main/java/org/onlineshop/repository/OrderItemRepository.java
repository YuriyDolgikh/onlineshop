package org.onlineshop.repository;

import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    Optional<OrderItem> findByOrderItemId(Integer orderItemId);

    List<OrderItem> findByOrder(Order order);
}

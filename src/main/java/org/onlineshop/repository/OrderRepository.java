package org.onlineshop.repository;

import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser(User user);
    List<Order> findByStatus(Order.Status status);
    Optional<Order> findByOrderIdAndUser(Integer orderId, User user);

}

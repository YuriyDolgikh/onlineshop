package org.onlineshop.repository;

import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByStatus(Order.Status status);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses AND o.createdAt > :createdAt")
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByStatusAndCreatedAtAfter(@Param("statuses") List<Order.Status> statuses,
                                              @Param("createdAt") LocalDateTime createdAt);

    Order findByUserAndStatus(User user, Order.Status status);
}

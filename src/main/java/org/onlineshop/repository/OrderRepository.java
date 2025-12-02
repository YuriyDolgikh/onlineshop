package org.onlineshop.repository;

import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser(User user);

    List<Order> findByStatus(Order.Status status);

    //    Optional<Order> findByOrderIdAndUser(Integer orderId, User user);
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses AND o.createdAt > :createdAt")
    List<Order> findByStatusAndCreatedAtAfter(@Param("statuses") List<Order.Status> statuses,
                                                @Param("createdAt") LocalDateTime createdAt);

    Order findByUserAndStatus(User user, Order.Status status);
//    List<Order> findByStatusAndCreatedAtAfter(List<Order.Status> statuses, LocalDateTime dateTime);
}

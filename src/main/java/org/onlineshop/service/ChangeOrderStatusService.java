package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.entity.Order;
import org.onlineshop.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeOrderStatusService {

    private final OrderRepository orderRepository;

    /**
     * Periodically processes the status of orders to move them to the next logical status.
     *
     * This method is executed at a fixed interval defined by a cron expression and is transactional by nature.
     * It retrieves a list of orders that are eligible for status updates based on their current status.
     *
     * For each eligible order:
     * - Orders with a status of PAID will be updated to IN_TRANSIT.
     * - Orders with a status of IN_TRANSIT will be updated to DELIVERED.
     * - No action is performed on orders with a status of DELIVERED or CANCELLED.
     *
     * If there are eligible orders, logs the number of orders processed and performs the updates.
     */
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void processOrderStatus() {
        List<Order> ordersForProcess = getOrdersForChangeStatus();
        if (!ordersForProcess.isEmpty()) {
            for (Order order : ordersForProcess) {
                setNextOrderStatus(order);
            }
        }
    }

    /**
     * Retrieves a list of orders eligible for a status change based on their current status.
     * Orders with the status PAID or IN_TRANSIT are included in the result.
     *
     * @return a list of orders that have a status of PAID or IN_TRANSIT
     */
    private List<Order> getOrdersForChangeStatus() {
        return orderRepository.findAll()
                .stream()
                .filter(order ->
                        (order.getStatus() == Order.Status.PAID || order.getStatus() == Order.Status.IN_TRANSIT))
                .toList();
    }

    /**
     * Updates the order's status to the next logical stage based on its current status.
     *
     * The transition rules are as follows:
     * - If the current status is PAID, it transitions to IN_TRANSIT.
     * - If the current status is IN_TRANSIT, it transitions to DELIVERED.
     * - If the current status is DELIVERED or CANCELLED, no action is performed.
     *
     * The method also updates the order's updatedAt field to the current timestamp and saves the updated order.
     *
     * @param order the order whose status is to be updated
     */
    private void setNextOrderStatus(Order order) {
        Order.Status orderStatus = order.getStatus();
        if (orderStatus == Order.Status.DELIVERED || orderStatus == Order.Status.CANCELLED) {
            return;
        }
        if (orderStatus == Order.Status.PAID) {
            order.setStatus(Order.Status.IN_TRANSIT);
        }
        if (orderStatus == Order.Status.IN_TRANSIT) {
            order.setStatus(Order.Status.DELIVERED);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}

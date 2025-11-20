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
     * Periodically processes the statuses of orders based on predefined criteria.
     * This method is scheduled to execute every 30 seconds.
     * <p>
     * The method retrieves orders in specific statuses (e.g., PAID, IN_TRANSIT)
     * that are eligible for status updates.
     * <p>
     * For each eligible order, the status is updated to the next logical status:
     * - PAID -> IN_TRANSIT
     * - IN_TRANSIT -> DELIVERED
     * <p>
     * Updates to the status are saved to the database, and a log message is output
     * indicating the newly updated status for each order.
     * <p>
     * This method is transactional to ensure consistency in the database
     * during the status update process.
     */
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void processOrderStatus() {
        List<Order> ordersForProcess = getOrdersForChangeStatus();
        if (!ordersForProcess.isEmpty()) {
            System.out.println("Orders for change status: " + ordersForProcess.size());
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
     * Updates the status of the given order to the next logical status based on its current status.
     * <p>
     * This method performs the following transitions for the provided order:
     * - If the current status is PAID, the status is updated to IN_TRANSIT.
     * - If the current status is IN_TRANSIT, the status is updated to DELIVERED.
     * <p>
     * No action is performed if the order's status is already DELIVERED or CANCELLED.
     * The method also updates the order's last updated timestamp and saves the changes
     * to the order repository.
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
        System.out.println("Changed order status to " + order.getStatus() + " for order with ID = " + order.getOrderId());
    }
}

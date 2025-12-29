package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.entity.Order;
import org.onlineshop.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeOrderStatusService {

    private final OrderRepository orderRepository;

    /**
     * Processes the status of orders in batch mode. This method is scheduled to execute
     * every 30 seconds. It retrieves eligible orders for status updates in paginated
     * batches, updates their statuses according to predefined transition rules, and
     * persists the changes to the database.
     *
     * The method performs the following steps:
     * 1. Fetches orders that are in eligible statuses (e.g., PAID or IN_TRANSIT) for processing.
     * 2. Iterates over the fetched orders and applies status updates based on the
     *    lifecycle rules, such as
     *      - PAID -> IN_TRANSIT
     *      - IN_TRANSIT -> DELIVERED
     * 3. Saves the updated orders back to the database in bulk for efficiency.
     * 4. Logs the number of processed orders for traceability.
     *
     * Pagination is used to iterate through orders in batches of a fixed size, ensuring
     * scalability and efficiency for large datasets. The process continues until there
     * are no more pages of orders left to process.
     *
     * This method is transactional to ensure consistency, so all changes within a single
     * batch are either committed together or rolled back in case of failure.
     */
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void processOrderStatus() {
        int page = 0;
        Page<Order> orderPage;
        final int BATCH_SIZE = 20;

        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE);
            orderPage = getOrdersForChangeStatus(pageable);

            if (!orderPage.getContent().isEmpty()) {
                List<Order> updatedOrders = new ArrayList<>();

                for (Order order : orderPage.getContent()) {
                    if (setNextOrderStatus(order)) {
                        updatedOrders.add(order);
                    }
                }

                if (!updatedOrders.isEmpty()) {
                    orderRepository.saveAll(updatedOrders);
                    log.info("Processed {} orders from page {}", updatedOrders.size(), page);
                }
            }
            page++;
        } while (orderPage.hasNext());
    }

    /**
     * Retrieves a paginated list of orders that are eligible for status updates.
     * The eligible statuses are PAID and IN_TRANSIT.
     *
     * @param pageable the pagination information, including page number and size
     * @return a paginated list of orders in eligible statuses for status updates
     */
    @Transactional(readOnly = true)
    protected Page<Order> getOrdersForChangeStatus(Pageable pageable) {
        List<Order.Status> processableStatuses = List.of(Order.Status.PAID, Order.Status.IN_TRANSIT);
        return orderRepository.findOrdersForStatusUpdate(processableStatuses, pageable);
    }

    /**
     * Updates the status of the given order to the next logical status in the workflow, if applicable.
     * The status transition rules are:
     * - PAID -> IN_TRANSIT
     * - IN_TRANSIT -> DELIVERED
     * If the status of the order cannot be transitioned to the next status, no changes are made.
     * The method also updates the order's last modified timestamp when a transition occurs.
     *
     * @param order the order whose status should be updated
     * @return true if the order's status was successfully updated, false otherwise
     */
    private boolean setNextOrderStatus(Order order) {
        Order.Status orderStatus = order.getStatus();

        if (orderStatus == Order.Status.PAID) {
            order.setStatus(Order.Status.IN_TRANSIT);
            order.setUpdatedAt(LocalDateTime.now());
            log.info("Order {} status updated to IN_TRANSIT", order.getOrderId());
            return true;
        } else if (orderStatus == Order.Status.IN_TRANSIT) {
            order.setStatus(Order.Status.DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
            log.info("Order {} status updated to DELIVERED", order.getOrderId());
            return true;
        }
        return false;
    }
}

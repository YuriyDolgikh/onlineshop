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

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void processOrderStatus() {
        List<Order> ordersForProcess = getOrdersForChangeStatus();
        for (Order order : ordersForProcess) {
            setNextOrderStatus(order);
        }
    }

    private List<Order> getOrdersForChangeStatus(){
        return orderRepository.findAll()
                .stream()
                .filter(order ->
                        (order.getStatus()== Order.Status.PAID || order.getStatus()== Order.Status.IN_TRANSIT))
                .toList();
    }

    private void setNextOrderStatus(Order order){
        Order.Status orderStatus=order.getStatus();
        if(orderStatus==Order.Status.DELIVERED || orderStatus==Order.Status.CANCELLED){
            return;
        }
        if(orderStatus==Order.Status.PAID){
            order.setStatus(Order.Status.IN_TRANSIT);
        }
        if(orderStatus==Order.Status.IN_TRANSIT){
            order.setStatus(Order.Status.DELIVERED);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}

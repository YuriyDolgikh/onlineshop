package org.onlineshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotBlank
    @Size(max = 100)
    private String deliveryAddress;

    @NotBlank
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and may start with +, length 7–15"
    )
    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum Status {
        OPEN,
        PENDING_PAYMENT,
        PAID,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }

    public enum DeliveryMethod {
        STANDARD,
        EXPRESS,
        SELF_DELIVERY
    }
}

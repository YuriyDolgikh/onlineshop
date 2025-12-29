package org.onlineshop.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "category")
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "product title is required and must be not blank")
    @Size(min = 3, max = 20)
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @URL
    @Column(length = 256)
    private String image;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}

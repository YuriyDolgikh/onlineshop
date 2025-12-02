package org.onlineshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    @Column(nullable = false, unique = true)
    public String categoryName;

    @URL
    @Column(length = 256)
    private String image;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();


}

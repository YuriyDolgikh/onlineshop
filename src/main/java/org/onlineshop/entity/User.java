package org.onlineshop.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @NotBlank
    @Size(min = 3, max = 15)
    @Column(name = "username")
    private String username;

    @Email(message = "Please provide a valid email address")
    @NotBlank
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(unique = true, nullable = false,name = "email")
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and may start with +, length 7–15"
    )
    @Column(unique = true, nullable = false,name = "phone_number")
    private String phoneNumber;

    @NotBlank
    @Column(name = "hash_password")
    private String hashPassword;

    @Column(nullable = false,name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false,name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ToString.Exclude
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "cart_id", referencedColumnName = "cartId")
    private Cart cart;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favourite> favourites = new HashSet<>();

    @Version
    private Integer version;

    public enum Role {
        ADMIN,
        MANAGER,
        USER,
    }

    public enum Status {
        NOT_CONFIRMED,
        CONFIRMED,
        DELETED
    }
}

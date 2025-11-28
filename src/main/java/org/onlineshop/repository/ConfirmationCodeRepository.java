package org.onlineshop.repository;

import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Integer> {
    Optional<ConfirmationCode> findByCode(String code);

    Optional<ConfirmationCode> findByUser(User user);

//    void deleteByUser(User user);
}

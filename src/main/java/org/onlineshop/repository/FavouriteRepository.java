package org.onlineshop.repository;

import org.onlineshop.entity.Favourite;
import org.onlineshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite,Integer> {

    List<Favourite> findByUser(User user);


}

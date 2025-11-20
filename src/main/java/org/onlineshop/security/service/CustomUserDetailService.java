package org.onlineshop.security.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.security.entity.MyUserToUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository repository;

    /**
     * Loads a user by their email address from the database and converts it to a UserDetails object.
     *
     * @param userEmail the email address of the user to be retrieved
     * @return a UserDetails object for the user with the given email address
     * @throws UsernameNotFoundException if no user with the specified email address is found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return repository.findByEmail(userEmail)
                .map(user -> new MyUserToUserDetails(user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + userEmail + " is not found"));
    }
}


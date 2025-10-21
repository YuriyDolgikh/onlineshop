package org.onlineshop.security.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.security.dto.AuthRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Generate a JWT token for a user with a username (email)
     *
     * @param request - object with username and password
     * @return String variable with JWT token
     */
    public String generateJwt(AuthRequestDto request) {
        try {
            customUserDetailService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new NotFoundException("User with email: " + request.getUsername() + " is not registered");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.createToken(request.getUsername());
    }
}

package org.onlineshop.security.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.security.dto.AuthRequestDto;
import org.onlineshop.security.entity.MyUserToUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        UserDetails userDetails;
        try {
          userDetails = customUserDetailService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new NotFoundException("User with email: " + request.getUsername() + " is not registered");
        }

        User user = ((MyUserToUserDetails)userDetails).getUser();
        if(user.getStatus().equals("DELETED")){
            throw new BadRequestException("User with email: " + request.getUsername() + " is deleted");
        }

        if(user.getStatus().equals("NOT_CONFIRMED")){
            throw new BadRequestException("User with email: " + request.getUsername() + " is not confirmed");
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

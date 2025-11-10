package org.onlineshop.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.onlineshop.security.exception.InvalidJwtException;
import org.onlineshop.security.service.CustomUserDetailService;
import org.onlineshop.security.service.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getTokenFromRequest(request);

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // get the username from JWT from request (this is 'email' in our case)
                String userName = jwtTokenProvider.getUsernameFromJwt(jwt);
                // create an object UserDetail, which knows Spring Security to fill it with our user data
                UserDetails userDetails = customUserDetailService.loadUserByUsername(userName);
                // create a necessary object from Spring Security to fill SecurityContext
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (InvalidJwtException e) {
            System.out.println("ERROR !!! " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("error: " + e.getMessage());
            return;
        }
        // definitely we need to apply changes to the object with the list of filters
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        /*
            If in the request there is a jwt, then in the request body will be a string
            that looks like: "Bearer askjhfgaskjhfgbas.asdfgareghaerhaerhaerh.arehgareharhaerhaerh"
            That is, we have to take from this line ALL to the end starting with the first character after "Bearer "
            begins from 7 characters in the line and to the end.
         */
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

package com.example.authentification.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, jakarta.servlet.ServletException {

        // Let CORS preflight (OPTIONS) pass through without JWT processing
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Log pour debugging
        String requestPath = request.getRequestURI();
        LOGGER.debug("Processing request: {}", requestPath);

        String authHeader = request.getHeader("Authorization");
        LOGGER.debug("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.debug("No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            LOGGER.debug("Extracted token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            String username = jwtService.extractUsername(token);
            LOGGER.debug("Extracted username from token: {}", username);

            var userDetails = userDetailsService.loadUserByUsername(username);
            LOGGER.debug("Loaded user details for: {}", username);

            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            LOGGER.debug("Authentication set for: {}", username);
        } catch (Exception ex) {
            // In case of token parsing/validation errors, don't throw here — continue chain
            // so that Spring Security can handle authentication failures properly
            LOGGER.error("JWT processing failed: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}


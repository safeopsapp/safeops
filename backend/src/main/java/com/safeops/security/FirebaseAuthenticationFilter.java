package com.safeops.security;

import com.google.firebase.auth.FirebaseToken;
import com.safeops.auth.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final FirebaseTokenVerifier tokenVerifier;
    private final AuthService authService;

    public FirebaseAuthenticationFilter(FirebaseTokenVerifier tokenVerifier, AuthService authService) {
        this.tokenVerifier = tokenVerifier;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = authHeader.substring(BEARER_PREFIX.length());

        try {
            FirebaseToken firebaseToken = tokenVerifier.verifyToken(idToken);
            SafeOpsPrincipal principal = authService.buildPrincipal(firebaseToken);
            SafeOpsUserDetails userDetails = new SafeOpsUserDetails(principal);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: firebaseUid={}", firebaseToken.getUid());

        } catch (FirebaseAuthenticationException e) {
            log.warn("Firebase authentication failed: {}", e.getMessage());
            // Continue without setting authentication - Spring Security will handle the 401
        } catch (Exception e) {
            log.error("Unexpected error during Firebase authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}

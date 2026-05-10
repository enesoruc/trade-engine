package com.brokerage.tradeengine.infrastructure.adapter.security;

import com.brokerage.tradeengine.application.port.AuthenticatedCustomerProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthenticatedCustomerProvider implements AuthenticatedCustomerProvider {

    @Override
    public String getAuthenticatedCustomerId() {
        return getAuthentication().getName();
    }

    @Override
    public boolean isAdmin() {
        return getAuthentication().getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

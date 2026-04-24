package com.airtel.inventory.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.airtel.inventory.domain.AppUser;
import com.airtel.inventory.service.AccountService;

@ControllerAdvice
public class GlobalModelAttributes {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AccountService accountService;

    public GlobalModelAttributes(AccountService accountService) {
        this.accountService = accountService;
    }

    @ModelAttribute("currentUser")
    public AppUser currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return accountService.findByUsername(authentication.getName());
    }

    @ModelAttribute("currentTimestamp")
    public String currentTimestamp() {
        return TIMESTAMP_FORMAT.format(LocalDateTime.now());
    }
}

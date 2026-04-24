package com.airtel.inventory.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.airtel.inventory.domain.AppUser;
import com.airtel.inventory.domain.UserRole;
import com.airtel.inventory.repository.AppUserRepository;
import com.airtel.inventory.web.form.RegistrationForm;

@Service
public class AccountService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser registerInventoryOfficer(RegistrationForm form) {
        validateRegistration(form);

        AppUser user = new AppUser();
        user.setFullName(form.getFullName().trim());
        user.setUsername(form.getUsername().trim().toLowerCase());
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole(UserRole.INVENTORY_OFFICER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser createAdminIfMissing(String fullName, String username, String email, String rawPassword) {
        return appUserRepository.findByUsernameIgnoreCase(username)
                .orElseGet(() -> {
                    AppUser admin = new AppUser();
                    admin.setFullName(fullName);
                    admin.setUsername(username.toLowerCase());
                    admin.setEmail(email.toLowerCase());
                    admin.setPasswordHash(passwordEncoder.encode(rawPassword));
                    admin.setRole(UserRole.ADMIN);
                    admin.setEnabled(true);
                    admin.setCreatedAt(LocalDateTime.now());
                    return appUserRepository.save(admin);
                });
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for " + username));
    }

    public String displayNameForUsername(String username) {
        return findByUsername(username).getFullName();
    }

    private void validateRegistration(RegistrationForm form) {
        if (!StringUtils.hasText(form.getFullName())) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!StringUtils.hasText(form.getUsername())) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (!StringUtils.hasText(form.getEmail())) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (!StringUtils.hasText(form.getPassword()) || form.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }
        if (appUserRepository.existsByUsernameIgnoreCase(form.getUsername().trim())) {
            throw new IllegalArgumentException("That username is already taken.");
        }
        if (appUserRepository.existsByEmailIgnoreCase(form.getEmail().trim())) {
            throw new IllegalArgumentException("That email address is already registered.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }
}

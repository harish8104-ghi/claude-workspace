package com.example.demo.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public DatabaseUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User account was not found"));
        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .build();
    }
}

package com.example.demo.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminBootstrap(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername.trim();
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminUsername.isBlank() && adminPassword.isBlank()) {
            logger.warn("No ADMIN account bootstrapped. Set ADMIN_USERNAME and ADMIN_PASSWORD to enable dashboard access.");
            return;
        }
        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("Set both ADMIN_USERNAME and ADMIN_PASSWORD to bootstrap an administrator.");
        }
        if (adminPassword.length() < 12 || adminPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("ADMIN_PASSWORD must be at least 12 characters and at most 72 UTF-8 bytes.");
        }
        if (adminUsername.length() < 3 || adminUsername.length() > 50
                || !adminUsername.matches("[a-zA-Z0-9][a-zA-Z0-9._-]*")) {
            throw new IllegalStateException("ADMIN_USERNAME must be 3-50 letters, digits, dots, underscores, or hyphens.");
        }

        String normalizedUsername = adminUsername.toLowerCase(Locale.ROOT);
        userAccountRepository.findByUsername(normalizedUsername).ifPresentOrElse(
                account -> {
                    account.setAdminCredentials(passwordEncoder.encode(adminPassword));
                    userAccountRepository.save(account);
                },
                () -> userAccountRepository.save(new UserAccount(
                        normalizedUsername,
                        passwordEncoder.encode(adminPassword),
                        AppRole.ADMIN)));
    }
}

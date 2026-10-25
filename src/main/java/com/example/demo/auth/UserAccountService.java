package com.example.demo.auth;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserAccountService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._-]{2,49}");
    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResult register(String username, String password) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            return RegistrationResult.INVALID_USERNAME;
        }
        if (password == null
                || password.length() < MIN_PASSWORD_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            return RegistrationResult.INVALID_PASSWORD;
        }

        String normalizedUsername = username.toLowerCase(Locale.ROOT);
        if (userAccountRepository.existsByUsername(normalizedUsername)) {
            return RegistrationResult.USERNAME_TAKEN;
        }

        try {
            userAccountRepository.saveAndFlush(new UserAccount(
                    normalizedUsername,
                    passwordEncoder.encode(password),
                    AppRole.USER));
        } catch (DataIntegrityViolationException exception) {
            return RegistrationResult.USERNAME_TAKEN;
        }
        return RegistrationResult.CREATED;
    }

    public enum RegistrationResult {
        CREATED,
        INVALID_USERNAME,
        INVALID_PASSWORD,
        USERNAME_TAKEN,
        REGISTRATION_FAILED
    }
}

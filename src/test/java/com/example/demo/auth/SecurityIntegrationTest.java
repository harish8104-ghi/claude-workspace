package com.example.demo.auth;

import com.example.demo.SpringJava25DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = SpringJava25DemoApplication.class,
        properties = {
                "spring.datasource.url=jdbc:sqlite:./target/security-test.db",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "app.admin.username=seed-admin",
                "app.admin.password=secure-admin-password"
        })
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void loginAndRegistrationPages_AreAvailableAndContainCsrfProtectedForms() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));
    }

    @Test
    void anonymousUsersAreRedirectedAndRegisteredUsersCannotViewTraffic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "viewer")
                        .param("password", "viewer-password-12"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"))
                .andReturn();
        mockMvc.perform(get("/login").param("registered", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Account created. Sign in to continue.")));

        MvcResult login = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "viewer")
                        .param("password", "viewer-password-12"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get("/").session((MockHttpSession) login.getRequest().getSession(false)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/index.html").with(user("viewer").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/traffic/stats")
                        .with(user("viewer").roles("USER")))
                .andExpect(status().isForbidden());

        UserAccount registeredUser = userAccountRepository.findByUsername("viewer").orElseThrow();
        assertEquals(AppRole.USER, registeredUser.getRole());
        assertNotEquals("viewer-password-12", registeredUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("viewer-password-12", registeredUser.getPasswordHash()));
    }

    @Test
    void bootstrappedAdminCanViewDashboardAndTrafficApi() throws Exception {
        UserAccount admin = userAccountRepository.findByUsername("seed-admin").orElseThrow();
        assertEquals(AppRole.ADMIN, admin.getRole());
        assertTrue(passwordEncoder.matches("secure-admin-password", admin.getPasswordHash()));

        mockMvc.perform(get("/").with(user("seed-admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("API traffic")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));

        mockMvc.perform(get("/api/traffic/stats").with(user("seed-admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void registrationRejectsInvalidCredentialsAndNeverGrantsAdminRole() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "ab")
                        .param("password", "short"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error=invalid_username"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "new-user")
                        .param("password", "short"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error=invalid_password"));

        assertTrue(userAccountRepository.findByUsername("new-user").isEmpty());
    }

    @Test
    void registrationHandlesDuplicateUsernamesWithAnInlineMessage() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "duplicate")
                        .param("password", "duplicate-password-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "DUPLICATE")
                        .param("password", "different-password-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error=username_taken"));

        mockMvc.perform(get("/register").param("error", "username_taken"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("That username is already registered.")));
    }
}

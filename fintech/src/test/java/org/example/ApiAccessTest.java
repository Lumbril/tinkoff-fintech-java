package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.User;
import org.example.entities.enums.Role;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAccessTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void unauthenticatedUserAccessTest() throws Exception {
        mockMvc.perform(get("/api/weather/Казань"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userAccessTest() throws Exception {
        String username = "user";
        String password = "12345";
        User u = createUserWithRole(username, password, Role.ROLE_USER);
        mockMvc.perform(get("/api/weather/Казань")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk());
        deleteUser(u);

        String usernameAdmin = "testAdmin";
        String passwordAdmin = "12345";
        u = createUserWithRole(usernameAdmin, passwordAdmin, Role.ROLE_ADMIN);
        mockMvc.perform(get("/api/weather/Казань")
                        .with(httpBasic(usernameAdmin, passwordAdmin)))
                .andExpect(status().isOk());
        deleteUser(u);
    }

    @Test
    public void userDeniedAccessTest() throws Exception {
        String username = "user";
        String password = "12345";
        User u = createUserWithRole(username, password, Role.ROLE_USER);
        mockMvc.perform(post("/api/weather/Казань")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\": 22.0, \"date\": \"2023-11-02T16:42:24.358Z\"}")
                        .with(httpBasic(username, password))
                )
                .andExpect(status().isForbidden());
        deleteUser(u);
    }

    @Test
    public void adminAccessTest() throws Exception {
        String usernameAdmin = "testAdmin";
        String passwordAdmin = "12345";
        User u = createUserWithRole(usernameAdmin, passwordAdmin, Role.ROLE_ADMIN);
        mockMvc.perform(post("/api/weather/Казань")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\": 22.0, \"date\": \"2023-11-02T16:42:24.358Z\"}")
                        .with(httpBasic(usernameAdmin, passwordAdmin))
                )
                .andExpect(status().isOk());
        deleteUser(u);
    }

    private User createUserWithRole(String username, String password, Role role) {
        return userRepository.save(User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .role(role)
                .build());
    }

    private void deleteUser(User user) {
        userRepository.delete(user);
    }
}

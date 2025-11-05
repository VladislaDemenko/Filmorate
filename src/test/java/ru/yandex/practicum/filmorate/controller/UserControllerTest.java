package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("test@mail.ru");
        validUser.setLogin("testlogin");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateValidUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@mail.ru"))
                .andExpect(jsonPath("$.login").value("testlogin"))
                .andExpect(jsonPath("$.name").value("testlogin")); // Должен использовать логин как имя
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() throws Exception {
        User user = new User();
        user.setEmail("friend@common.ru");
        user.setLogin("common");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 8, 20));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("common"));
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsNull() throws Exception {
        User user = new User();
        user.setEmail("friend@common.ru");
        user.setLogin("common");
        user.setBirthday(LocalDate.of(2000, 8, 20));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("common"));
    }

    @Test
    void shouldRejectUserWithInvalidEmail() throws Exception {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUserWithEmptyEmail() throws Exception {
        User user = new User();
        user.setEmail("");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUserWithEmptyLogin() throws Exception {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUserWithLoginContainingSpaces() throws Exception {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUserWithFutureBirthday() throws Exception {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(2001, 1, 1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@mail.ru"))
                .andExpect(jsonPath("$.login").value("updatedlogin"))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        User user = new User();
        user.setId(9999L);
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void shouldPreserveNameWhenProvided() throws Exception {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName("Custom Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Custom Name"));
    }
}
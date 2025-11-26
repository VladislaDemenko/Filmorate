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
    private User friendUser;

    @BeforeEach
    void setUp() throws Exception {
        validUser = new User();
        validUser.setEmail("test@mail.ru");
        validUser.setLogin("testlogin");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        friendUser = new User();
        friendUser.setEmail("friend@mail.ru");
        friendUser.setLogin("friendlogin");
        friendUser.setBirthday(LocalDate.of(2001, 1, 1));

        // Создаем пользователей для тестов друзей
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();
        validUser = objectMapper.readValue(userResponse, User.class);

        String friendResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendUser)))
                .andReturn().getResponse().getContentAsString();
        friendUser = objectMapper.readValue(friendResponse, User.class);
    }

    @Test
    void shouldCreateValidUser() throws Exception {
        User newUser = new User();
        newUser.setEmail("new@mail.ru");
        newUser.setLogin("newlogin");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("new@mail.ru"))
                .andExpect(jsonPath("$.login").value("newlogin"))
                .andExpect(jsonPath("$.name").value("newlogin"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", validUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validUser.getId()))
                .andExpect(jsonPath("$.email").value("test@mail.ru"))
                .andExpect(jsonPath("$.login").value("testlogin"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/{id}", 9999L))
                .andExpect(status().isNotFound());
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
        User updatedUser = new User();
        updatedUser.setId(validUser.getId());
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
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)); // Два пользователя созданы в setUp
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

    @Test
    void shouldAddFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAddingFriendToNonExistentUser() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", 9999L, friendUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenAddingNonExistentFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", validUser.getId(), 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        // Сначала добавляем друга
        mockMvc.perform(put("/users/{id}/friends/{friendId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk());

        // Затем удаляем
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetFriends() throws Exception {
        // Добавляем друга
        mockMvc.perform(put("/users/{id}/friends/{friendId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", validUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(friendUser.getId()));
    }

    @Test
    void shouldReturnEmptyFriendsList() throws Exception {
        mockMvc.perform(get("/users/{id}/friends", validUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        // Создаем третьего пользователя
        User commonFriend = new User();
        commonFriend.setEmail("common@mail.ru");
        commonFriend.setLogin("commonlogin");
        commonFriend.setBirthday(LocalDate.of(2002, 1, 1));

        String commonResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commonFriend)))
                .andReturn().getResponse().getContentAsString();
        commonFriend = objectMapper.readValue(commonResponse, User.class);

        // Добавляем общего друга обоим пользователям
        mockMvc.perform(put("/users/{id}/friends/{friendId}", validUser.getId(), commonFriend.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/{id}/friends/{friendId}", friendUser.getId(), commonFriend.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(commonFriend.getId()));
    }

    @Test
    void shouldReturnEmptyCommonFriends() throws Exception {
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", validUser.getId(), friendUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
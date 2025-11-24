package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film validFilm;
    private User validUser;

    @BeforeEach
    void setUp() throws Exception {
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

        validUser = new User();
        validUser.setEmail("test@mail.ru");
        validUser.setLogin("testlogin");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andReturn().getResponse().getContentAsString();

        validUser = objectMapper.readValue(userResponse, User.class);
    }

    @Test
    void shouldCreateValidFilm() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Valid Film"))
                .andExpect(jsonPath("$.description").value("Valid description"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void shouldGetFilmById() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);

        mockMvc.perform(get("/films/{id}", createdFilm.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdFilm.getId()))
                .andExpect(jsonPath("$.name").value("Valid Film"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentFilm() throws Exception {
        mockMvc.perform(get("/films/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectFilmWithEmptyName() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectFilmWithTooLongDescription() throws Exception {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectFilmWithNegativeDuration() throws Exception {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(response, Film.class);

        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Film"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.duration").value(150));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentFilm() throws Exception {
        Film film = new Film();
        film.setId(9999L);
        film.setName("Non-existent Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllFilms() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void shouldAddLikeToFilm() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(filmResponse, Film.class);

        mockMvc.perform(put("/films/{id}/like/{userId}", createdFilm.getId(), validUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenAddingLikeToNonExistentFilm() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", 9999L, validUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenAddingLikeFromNonExistentUser() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(filmResponse, Film.class);

        mockMvc.perform(put("/films/{id}/like/{userId}", createdFilm.getId(), 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveLikeFromFilm() throws Exception {
        String filmResponse = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Film createdFilm = objectMapper.readValue(filmResponse, Film.class);

        mockMvc.perform(put("/films/{id}/like/{userId}", createdFilm.getId(), validUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/{id}/like/{userId}", createdFilm.getId(), validUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetPopularFilms() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated());

        Film film2 = new Film();
        film2.setName("Another Film");
        film2.setDescription("Another description");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetPopularFilmsWithCustomCount() throws Exception {
        for (int i = 0; i < 5; i++) {
            Film film = new Film();
            film.setName("Film " + i);
            film.setDescription("Description " + i);
            film.setReleaseDate(LocalDate.of(2000 + i, 1, 1));
            film.setDuration(100 + i);

            mockMvc.perform(post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(film)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/films/popular?count=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void shouldReturnEmptyListWhenNoFilms() throws Exception {
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
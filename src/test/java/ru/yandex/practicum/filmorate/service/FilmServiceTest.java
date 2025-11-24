package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmService filmService;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1L);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldAddLike() {
        when(filmStorage.getById(1L)).thenReturn(Optional.of(film));
        when(userStorage.existsById(1L)).thenReturn(true);

        filmService.addLike(1L, 1L);

        verify(filmStorage).getById(1L);
        verify(userStorage).existsById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAddingLikeToNonExistentFilm() {
        when(filmStorage.getById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> filmService.addLike(1L, 1L));
    }

    @Test
    void shouldThrowExceptionWhenAddingLikeFromNonExistentUser() {
        when(filmStorage.getById(1L)).thenReturn(Optional.of(film));
        when(userStorage.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> filmService.addLike(1L, 1L));
    }

    @Test
    void shouldRemoveLike() {
        when(filmStorage.getById(1L)).thenReturn(Optional.of(film));
        when(userStorage.existsById(1L)).thenReturn(true);

        filmService.addLike(1L, 1L);
        filmService.removeLike(1L, 1L);

        verify(filmStorage, times(2)).getById(1L);
        verify(userStorage, times(2)).existsById(1L);
    }

    @Test
    void shouldGetPopularFilmsWithDefaultCount() {
        when(filmStorage.getAll()).thenReturn(Collections.emptyList());

        List<Film> popularFilms = filmService.getPopularFilms(null);

        assertTrue(popularFilms.isEmpty());
    }
}
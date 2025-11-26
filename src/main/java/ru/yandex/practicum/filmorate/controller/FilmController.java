package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private static final String FILM_ID_PATH = "/{id}";
    private static final String LIKE_PATH = FILM_ID_PATH + "/like/{userId}";
    private static final String POPULAR_PATH = "/popular";

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(FILM_ID_PATH)
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping(LIKE_PATH)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping(LIKE_PATH)
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка с фильма {} от пользователя {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping(POPULAR_PATH)
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
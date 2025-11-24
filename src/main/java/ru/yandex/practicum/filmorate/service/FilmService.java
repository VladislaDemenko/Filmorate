package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final Map<Long, Set<Long>> likes = new HashMap<>();

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Фильм с id %d не найден", id)));
    }

    public void addLike(Long  filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (!userStorage.existsById(userId)) {
            throw  new NoSuchElementException(String.format("Пользователь с id &d не найден", userId));
        }
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (!userStorage.existsById(filmId)) {
            throw new NoSuchElementException(String.format("Пользователь с id %d не найден", userId));
        }
        if (likes.containsKey(filmId)) {
            likes.get(filmId).remove(userId);
            log.info("Пользователь с id {} удалил лайк с фильма id {}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;

        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = getLikesCount(f1.getId());
                    int likes2 = getLikesCount(f2.getId());
                    return Integer.compare(likes2, likes1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private int getLikesCount(Long filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}

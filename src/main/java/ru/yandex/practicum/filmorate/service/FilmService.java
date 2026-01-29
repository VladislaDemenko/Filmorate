package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        validateMpaExists(film);
        validateGenresExist(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        validateMpaExists(film);
        validateGenresExist(film);
        getFilmById(film.getId()); // Проверяем существование фильма
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count == null ? 10 : count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new IllegalArgumentException("Название фильма обязательно");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new IllegalArgumentException("Описание не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("Продолжительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("Рейтинг MPA обязателен");
        }
        if (film.getMpa().getId() == null) {
            throw new IllegalArgumentException("ID рейтинга MPA обязателен");
        }
    }

    private void validateMpaExists(Film film) {
        mpaDbStorage.getMpaById(film.getMpa().getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Рейтинг MPA с id " + film.getMpa().getId() + " не найден"));
    }

    private void validateGenresExist(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Genre> allGenres = genreDbStorage.getAllGenres();
        Set<Long> existingGenreIds = allGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        for (Genre genre : film.getGenres()) {
            if (!existingGenreIds.contains(genre.getId())) {
                throw new NoSuchElementException(
                        "Жанр с id " + genre.getId() + " не найден. Доступные ID: " + existingGenreIds);
            }
        }
    }
}
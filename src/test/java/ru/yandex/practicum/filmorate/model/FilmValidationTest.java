package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private final Validator validator;

    public FilmValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCorrectFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Корректный фильм не должен иметь нарушений валидации");
    }

    @Test
    void shouldRejectFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с пустым названием должен быть отклонен");
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("Название фильма не может быть пустым")));
    }

    @Test
    void shouldRejectFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с описанием длиннее 200 символов должен быть отклонен");
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("Максимальная длина описания — 200 символов")));
    }

    @Test
    void shouldRejectFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с отрицательной продолжительностью должен быть отклонен");
        assertTrue(violations.stream().anyMatch(violation -> violation.getMessage().contains("Продолжительность фильма должна быть положительным числом")));
    }

    @Test
    void shouldAcceptFilmWithMaxDescriptionLength() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Фильм с описанием длиной 200 символов должен быть принят");
    }
}
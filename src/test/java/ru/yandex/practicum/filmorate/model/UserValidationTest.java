package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private final Validator validator;

    public UserValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCorrectUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Корректный пользователь не должен иметь нарушений валидации");
    }

    @Test
    void shouldRejectUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Пользователь с невалидным email должен быть отклонен");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Электронная почта должна содержать символ @")));
    }

    @Test
    void shouldRejectUserWithEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Пользователь с пустым email должен быть отклонен");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Электронная почта не может быть пустой")));
    }

    @Test
    void shouldRejectUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Пользователь с пустым логином должен быть отклонен");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Логин не может быть пустым")));
    }

    @Test
    void shouldRejectUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Пользователь с логином содержащим пробелы должен быть отклонен");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Логин не может содержать пробелы")));
    }

    @Test
    void shouldRejectUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validlogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Пользователь с датой рождения в будущем должен быть отклонен");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата рождения не может быть в будущем")));
    }

    @Test
    void shouldAcceptUserWithNullName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName(null);
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Пользователь с null именем должен быть принят");
    }

    @Test
    void shouldAcceptUserWithEmptyName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Пользователь с пустым именем должен быть принят");
    }

    @Test
    void shouldAcceptUserWithValidLoginWithoutSpaces() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("valid_login123");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Пользователь с логином без пробелов должен быть принят");
    }
}
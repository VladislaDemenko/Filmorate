package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей. Кол-во на данный момент: {}", users.size());
        return users.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        user.setId(currentId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Для пользователя с id {} установлено имя из логина{}: ", user.getId(), user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return  user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Попытка обновления несуществующего пользовтеля с id: {}", user.getId());
            throw new NoSuchElementException("Пользователь с id: " + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь {}", user);
        return user;
    }
}

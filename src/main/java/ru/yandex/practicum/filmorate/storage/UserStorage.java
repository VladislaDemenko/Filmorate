package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAllUsers();
    Optional<User> getUserById();
    User createUser(User user);
    User uodateUser(User user);
    void deleteUser(Long id);
}

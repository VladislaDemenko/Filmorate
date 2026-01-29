package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id " + id + " не найден"));
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserById(user.getId()); // Проверяем существование пользователя
        return userStorage.updateUser(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        // Проверяем, не пытается ли пользователь добавить самого себя в друзья
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить самого себя в друзья");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
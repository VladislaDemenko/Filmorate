package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    List<User> getFriends(Long userId);
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    List<User> getCommonFriends(Long userId, Long otherUserId);
}
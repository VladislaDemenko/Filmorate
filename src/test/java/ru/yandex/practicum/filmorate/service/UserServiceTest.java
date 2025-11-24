package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(2001, 1, 1));

        user3 = new User();
        user3.setId(3L);
        user3.setEmail("user3@mail.ru");
        user3.setLogin("user3");
        user3.setBirthday(LocalDate.of(2002, 1, 1));
    }

    @Test
    void shouldAddFriend() {
        when(userStorage.getById(1L)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));

        userService.addFriend(1L, 2L);

        verify(userStorage).getById(1L);
        verify(userStorage).getById(2L);
    }

    @Test
    void shouldThrowExceptionWhenAddingFriendToNonExistentUser() {
        when(userStorage.getById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.addFriend(1L, 2L));
    }

    @Test
    void shouldRemoveFriend() {
        when(userStorage.getById(1L)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));

        userService.addFriend(1L, 2L);
        userService.removeFriend(1L, 2L);

        verify(userStorage, times(2)).getById(1L);
        verify(userStorage, times(2)).getById(2L);
    }

    @Test
    void shouldGetFriends() {
        when(userStorage.getById(1L)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3L)).thenReturn(Optional.of(user3));

        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);

        List<User> friends = userService.getFriends(1L);

        assertEquals(2, friends.size());
    }

    @Test
    void shouldGetCommonFriends() {
        when(userStorage.getById(1L)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));
        when(userStorage.getById(3L)).thenReturn(Optional.of(user3));
        when(userStorage.getById(3L)).thenReturn(Optional.of(user3));

        userService.addFriend(1L, 3L);
        userService.addFriend(2L, 3L);

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size());
        assertEquals(3L, commonFriends.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFriends() {
        when(userStorage.getById(1L)).thenReturn(Optional.of(user1));
        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);

        assertTrue(commonFriends.isEmpty());
    }
}
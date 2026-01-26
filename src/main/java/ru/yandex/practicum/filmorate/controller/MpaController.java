package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaDbStorage mpaStorage;

    @GetMapping
    public List<MpaRating> getAllMpa() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaStorage.getAllMpa();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable Long id) {
        log.info("Получен запрос на получение рейтинга MPA с id: {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NoSuchElementException("Рейтинг MPA с id " + id + " не найден"));
    }
}
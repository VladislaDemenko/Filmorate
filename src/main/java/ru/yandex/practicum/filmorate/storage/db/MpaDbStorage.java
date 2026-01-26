package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.db.mapper.MpaMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper = new MpaMapper();

    public List<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    public Optional<MpaRating> getMpaById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<MpaRating> mpaRatings = jdbcTemplate.query(sql, mpaMapper, id);
        return mpaRatings.isEmpty() ? Optional.empty() : Optional.of(mpaRatings.get(0));
    }
}
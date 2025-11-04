package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой!")
    @Email(message = "Электронная почта должна содержать символ '@'!")
    private String email;

    @NotBlank(message = "Логин не может быть пустым!")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы!")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть датой, которая еще не наступила!")
    private LocalDate birthday;
}

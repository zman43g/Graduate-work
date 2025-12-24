package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель для авторизации")
public class Login {
    @Schema(description = "Логин", minLength = 4, maxLength = 32, required = true, example = "user@yandex.ru")
    private String username;

    @Schema(description = "Пароль", minLength = 8, maxLength = 16, required = true, example = "password")
    private String password;
}
package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель для регистрации")
public class Register {
    @Schema(description = "Логин", minLength = 4, maxLength = 32, required = true, example = "user@example.com")
    private String username;

    @Schema(description = "Пароль", minLength = 8, maxLength = 16, required = true, example = "password123")
    private String password;

    @Schema(description = "Имя", minLength = 2, maxLength = 16, required = true, example = "Эдуард")
    private String firstName;

    @Schema(description = "Фамилия", minLength = 2, maxLength = 16, required = true, example = "Шуликов")
    private String lastName;

    @Schema(description = "Телефон", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", required = true, example = "+7 (999) 333-22-66")
    private String phone;

    @Schema(description = "Роль пользователя", required = true, example = "USER")
    private Role role;
}
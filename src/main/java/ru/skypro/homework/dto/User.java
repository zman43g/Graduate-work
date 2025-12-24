package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель пользователя")
public class User {
    @Schema(description = "ID пользователя", example = "1")
    private Integer id;

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Schema(description = "Телефон пользователя", example = "+7 (999) 123-45-67")
    private String phone;

    @Schema(description = "Роль пользователя")
    private Role role;

    @Schema(description = "URL аватара пользователя", example = "/users/1/image")
    private String image;
}
package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роли пользователей")
public enum Role {
    @Schema(description = "Пользователь")
    USER,

    @Schema(description = "Администратор")
    ADMIN
}

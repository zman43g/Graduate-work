package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель для обновления данных пользователя")
public class UpdateUser {
    @Schema(description = "Имя пользователя", minLength = 2, maxLength = 16, example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", minLength = 2, maxLength = 16, example = "Иванов")
    private String lastName;

    @Schema(description = "Телефон пользователя", pattern = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", example = "+7 (999) 999-99-11")
    private String phone;
}
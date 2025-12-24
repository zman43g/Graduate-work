package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель расширенной информации об объявлении")
public class ExtendedAd {
    @Schema(description = "ID объявления", example = "1")
    private Integer pk;

    @Schema(description = "Имя", example = "Иван")
    private String authorFirstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String authorLastName;

    @Schema(description = "Описание объявления", minLength = 8, maxLength = 64, example = "Корова рыжая молодая")
    private String description;

    @Schema(description = "Email", example = "user@yandex.ru")
    private String email;

    @Schema(description = "URL изображения объявления", example = "/ads/1/image")
    private String image;

    @Schema(description = "Телефон", example = "+7 (999) 123-45-67")
    private String phone;

    @Schema(description = "Цена", minimum = "0", maximum = "10000000", example = "5000")
    private Integer price;

    @Schema(description = "Заголовок", minLength = 4, maxLength = 32, example = "Продам корову")
    private String title;
}
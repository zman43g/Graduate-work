package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель для создания или обновления объявления")
public class CreateOrUpdateAd {
    @Schema(description = "Заголовок", minLength = 4, maxLength = 32, required = true, example = "Продам корову")
    private String title;

    @Schema(description = "Цена", minimum = "0", maximum = "10000000", required = true, example = "7000")
    private Integer price;

    @Schema(description = "Описание", minLength = 8, maxLength = 64, required = true, example = "Молодая корова рыжая")
    private String description;
}
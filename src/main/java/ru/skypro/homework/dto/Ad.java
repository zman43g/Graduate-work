package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "Модель объявления")
public class Ad {
    @Schema(description = "ID автора объявления", example = "1")
    private Integer author;

    @Schema(description = "URL изображения объявления", example = "/ads/1/image")
    private String image;

    @Schema(description = "ID объявления", example = "1")
    private Integer pk;

    @Schema(description = "Цена объявления", minimum = "0", maximum = "10000000", example = "5000")
    private Integer price;

    @Schema(description = "Заголовок объявления", minLength = 4, maxLength = 32, example = "Продам ноутбук")
    private String title;
}

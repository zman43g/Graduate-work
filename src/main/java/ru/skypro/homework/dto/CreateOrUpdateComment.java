package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель для создания или обновления комментария")
public class CreateOrUpdateComment {
    @Schema(description = "Текст комментария", required = true, example = "Доброго дня! Еще продаете?")
    private String text;
}
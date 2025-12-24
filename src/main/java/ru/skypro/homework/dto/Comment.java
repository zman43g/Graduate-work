package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель комментария")
public class Comment {
    @Schema(description = "ID автора комментария", example = "1")
    private Integer author;

    @Schema(description = "URL аватара автора", example = "/users/1/image")
    private String authorImage;

    @Schema(description = "Имя автора", example = "Эдуард")
    private String authorFirstName;

    @Schema(description = "Время создания комментария в миллисекундах", example = "1672531200000")
    private Long createdAt;

    @Schema(description = "ID комментария", example = "1")
    private Integer pk;

    @Schema(description = "Текст комментария", example = "Доброго дня!")
    private String text;
}
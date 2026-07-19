package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "Изображения", description = "API для доступа к изображениям")
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "Получение изображения пользователя",
            description = "Получение аватара пользователя по пути",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение получено",
                            content = @Content(mediaType = "image/*")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Изображение не найдено"
                    )
            }
    )
    @GetMapping("/users/{imagePath:.+}")
    public ResponseEntity<byte[]> getUserImage(
            @Parameter(description = "Путь к изображению", required = true)
            @PathVariable String imagePath) {

        try {
            byte[] imageData = imageService.getUserImageData("users/" + imagePath);
            String contentType = imageService.getImageContentType("users/" + imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageData);

        } catch (IOException e) {
            log.error("Ошибка при получении изображения пользователя: {}", imagePath, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Получение изображения объявления",
            description = "Получение изображения объявления по пути",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение получено",
                            content = @Content(mediaType = "image/*")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Изображение не найдено"
                    )
            }
    )
    @GetMapping("/ads/{imagePath:.+}")
    public ResponseEntity<byte[]> getAdImage(
            @Parameter(description = "Путь к изображению", required = true)
            @PathVariable String imagePath) {

        try {
            byte[] imageData = imageService.getAdImageData("ads/" + imagePath);
            String contentType = imageService.getImageContentType("ads/" + imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageData);

        } catch (IOException e) {
            log.error("Ошибка при получении изображения объявления: {}", imagePath, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Получение изображения по полному пути",
            description = "Получение изображения по полному пути (для обратной совместимости)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение получено",
                            content = @Content(mediaType = "image/*")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Изображение не найдено"
                    )
            }
    )
    @GetMapping("/{type}/{imagePath:.+}")
    public ResponseEntity<byte[]> getImage(
            @Parameter(description = "Тип изображения (users/ads)", required = true)
            @PathVariable String type,
            @Parameter(description = "Путь к изображению", required = true)
            @PathVariable String imagePath) {

        try {
            String fullPath = type + "/" + imagePath;
            byte[] imageData;

            if ("users".equals(type)) {
                imageData = imageService.getUserImageData(fullPath);
            } else if ("ads".equals(type)) {
                imageData = imageService.getAdImageData(fullPath);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String contentType = imageService.getImageContentType(fullPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageData);

        } catch (IOException e) {
            log.error("Ошибка при получении изображения: {}/{}", type, imagePath, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
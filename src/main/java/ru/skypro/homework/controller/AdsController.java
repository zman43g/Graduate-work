package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.io.IOException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Tag(name = "Объявления", description = "API для управления объявлениями и комментариями")
public class AdsController {

    private final AdsService adsService;

    @Operation(
            summary = "Получение всех объявлений",
            description = "Получение списка всех объявлений в системе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список объявлений получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Ads.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = adsService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @Operation(
            summary = "Добавление объявления",
            description = "Создание нового объявления с изображением",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Объявление создано",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Ad.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @Parameter(description = "Данные объявления", required = true)
            @RequestPart("properties") CreateOrUpdateAd properties,
            @Parameter(description = "Изображение объявления", required = true)
            @RequestPart("image") MultipartFile image,
            Authentication authentication) throws IOException {
        Ad ad = adsService.addAd(properties, image, authentication.getName());
        return ad != null ? ResponseEntity.status(HttpStatus.CREATED).body(ad) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Получение комментариев объявления",
            description = "Получение списка комментариев к конкретному объявлению",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарии получены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Comments.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id) {
        Comments comments = adsService.getComments(id);
        return comments != null ? ResponseEntity.ok(comments) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(
            summary = "Добавление комментария к объявлению",
            description = "Создание нового комментария к объявлению",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий добавлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Comment.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Данные комментария", required = true)
            @RequestBody CreateOrUpdateComment createOrUpdateComment,
            Authentication authentication) {
        Comment comment = adsService.addComment(id, createOrUpdateComment, authentication.getName());
        return comment != null ? ResponseEntity.ok(comment) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Получение информации об объявлении",
            description = "Получение расширенной информации об объявлении",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация получена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExtendedAd.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id) {
        ExtendedAd ad = adsService.getAd(id);
        return ad != null ? ResponseEntity.ok(ad) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(
            summary = "Удаление объявления",
            description = "Удаление объявления по ID",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Объявление удалено"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id,
            Authentication authentication) {
        boolean isDeleted = adsService.removeAd(id, authentication.getName());
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Обновление информации об объявлении",
            description = "Обновление данных объявления",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Объявление обновлено",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Ad.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Данные для обновления", required = true)
            @RequestBody CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication) {
        Ad ad = adsService.updateAd(id, createOrUpdateAd, authentication.getName());
        return ad != null ? ResponseEntity.ok(ad) :
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            description = "Получение списка объявлений текущего пользователя",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список объявлений получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Ads.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        Ads ads = adsService.getUserAds(authentication.getName());
        return ResponseEntity.ok(ads);
    }

    @Operation(
            summary = "Обновление картинки объявления",
            description = "Загрузка нового изображения для объявления",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение обновлено",
                            content = @Content(mediaType = "application/octet-stream")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdImage(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Файл изображения", required = true)
            @RequestParam("image") MultipartFile image,
            Authentication authentication) throws IOException {
        byte[] imageData = adsService.updateAdImage(id, image, authentication.getName());
        return imageData != null ?
                ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(imageData) :
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(
            summary = "Удаление комментария",
            description = "Удаление комментария по ID",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Комментарий удален"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден"
                    )
            }
    )
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("adId") Integer adId,
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable("commentId") Integer commentId,
            Authentication authentication) {
        boolean isDeleted = adsService.deleteComment(adId, commentId, authentication.getName());
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(
            summary = "Обновление комментария",
            description = "Обновление текста комментария",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий обновлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Comment.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден"
                    )
            }
    )
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable("adId") Integer adId,
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable("commentId") Integer commentId,
            @Parameter(description = "Данные для обновления", required = true)
            @RequestBody CreateOrUpdateComment createOrUpdateComment,
            Authentication authentication) {
        Comment comment = adsService.updateComment(adId, commentId, createOrUpdateComment, authentication.getName());
        return comment != null ? ResponseEntity.ok(comment) :
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
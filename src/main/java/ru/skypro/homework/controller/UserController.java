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
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

import java.io.IOException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Обновление пароля",
            description = "Смена пароля текущего пользователя",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пароль успешно обновлен"
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
                            responseCode = "400",
                            description = "Неверные данные запроса"
                    )
            }
    )
    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(
            @Parameter(description = "Данные для смены пароля", required = true)
            @RequestBody NewPassword newPassword,
            Authentication authentication) {
        try {
            boolean isUpdated = userService.updatePassword(newPassword, authentication.getName());
            if (isUpdated) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error("Ошибка при обновлении пароля", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            description = "Получение данных текущего пользователя",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация получена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        User user = userService.getUserInfo(authentication.getName());
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Обновление информации об авторизованном пользователе",
            description = "Обновление данных текущего пользователя",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация обновлена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateUser.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(
            @Parameter(description = "Данные для обновления", required = true)
            @RequestBody UpdateUser updateUser,
            Authentication authentication) {
        UpdateUser updatedUser = userService.updateUserInfo(updateUser, authentication.getName());
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            description = "Загрузка нового аватара для текущего пользователя",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Аватар обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserImage(
            @Parameter(description = "Файл изображения", required = true)
            @RequestParam("image") MultipartFile image,
            Authentication authentication) throws IOException {
        boolean isUpdated = userService.updateUserImage(image, authentication.getName());
        return isUpdated ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
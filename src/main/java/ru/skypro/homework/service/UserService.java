package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.io.IOException;

public interface UserService {

    /**
     * Обновление пароля пользователя
     * @param newPassword DTO с текущим и новым паролем
     * @param username имя пользователя
     * @return true если пароль успешно обновлен
     */
    boolean updatePassword(NewPassword newPassword, String username);

    /**
     * Получение информации о пользователе
     * @param username имя пользователя
     * @return DTO пользователя
     */
    User getUserInfo(String username);

    /**
     * Обновление информации о пользователе
     * @param updateUser DTO с обновленными данными
     * @param username имя пользователя
     * @return обновленный DTO пользователя
     */
    UpdateUser updateUserInfo(UpdateUser updateUser, String username);

    /**
     * Обновление аватара пользователя
     * @param image файл изображения
     * @param username имя пользователя
     * @return true если аватар успешно обновлен
     * @throws IOException если произошла ошибка при работе с файлом
     */
    boolean updateUserImage(MultipartFile image, String username) throws IOException;
}

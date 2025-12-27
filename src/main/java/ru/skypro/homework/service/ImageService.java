package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {

    /**
     * Сохранение изображения пользователя (аватар)
     * @param image файл изображения
     * @param userId ID пользователя
     * @return путь к сохраненному файлу
     * @throws IOException если произошла ошибка при сохранении
     */
    String saveUserImage(MultipartFile image, Integer userId) throws IOException;

    /**
     * Сохранение изображения объявления
     * @param image файл изображения
     * @return путь к сохраненному файлу
     * @throws IOException если произошла ошибка при сохранении
     */
    String saveAdImage(MultipartFile image) throws IOException;

    /**
     * Получение данных изображения пользователя
     * @param imagePath путь к изображению
     * @return байты изображения
     * @throws IOException если произошла ошибка при чтении
     */
    byte[] getUserImageData(String imagePath) throws IOException;

    /**
     * Получение данных изображения объявления
     * @param imagePath путь к изображению
     * @return байты изображения
     * @throws IOException если произошла ошибка при чтении
     */
    byte[] getAdImageData(String imagePath) throws IOException;

    /**
     * Удаление изображения пользователя
     * @param imagePath путь к изображению
     */
    void deleteUserImage(String imagePath);

    /**
     * Удаление изображения объявления
     * @param imagePath путь к изображению
     */
    void deleteAdImage(String imagePath);

    /**
     * Получение URL для доступа к изображению пользователя
     * @param imagePath путь к изображению
     * @return публичный URL
     */
    String getUserImageUrl(String imagePath);

    /**
     * Получение URL для доступа к изображению объявления
     * @param imagePath путь к изображению
     * @return публичный URL
     */
    String getAdImageUrl(String imagePath);

    /**
     * Получение MIME-типа изображения
     * @param imagePath путь к изображению
     * @return MIME-тип (например, "image/jpeg")
     */
    String getImageContentType(String imagePath);

    /**
     * Проверка существования изображения
     * @param imagePath путь к изображению
     * @return true если изображение существует
     */
    boolean imageExists(String imagePath);

    /**
     * Получение размера изображения
     * @param imagePath путь к изображению
     * @return размер в байтах
     * @throws IOException если произошла ошибка при чтении
     */
    long getImageSize(String imagePath) throws IOException;
}

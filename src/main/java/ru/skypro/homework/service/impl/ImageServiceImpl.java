package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.ImageService;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${app.image.storage.path:./images}")
    private String storagePath;

    @Value("${app.image.base-url:http://localhost:8080/images}")
    private String baseUrl;

    private Path userImagesDir;
    private Path adImagesDir;

    @PostConstruct
    public void init() {
        try {
            // Создаем базовую директорию
            Path baseDir = Paths.get(storagePath);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                log.info("Создана базовая директория для изображений: {}", baseDir.toAbsolutePath());
            }

            // Создаем директорию для изображений пользователей
            userImagesDir = baseDir.resolve("users");
            if (!Files.exists(userImagesDir)) {
                Files.createDirectories(userImagesDir);
                log.info("Создана директория для изображений пользователей: {}", userImagesDir.toAbsolutePath());
            }

            // Создаем директорию для изображений объявлений
            adImagesDir = baseDir.resolve("ads");
            if (!Files.exists(adImagesDir)) {
                Files.createDirectories(adImagesDir);
                log.info("Создана директория для изображений объявлений: {}", adImagesDir.toAbsolutePath());
            }

        } catch (IOException e) {
            log.error("Ошибка при инициализации директорий для изображений", e);
            throw new RuntimeException("Не удалось инициализировать хранилище изображений", e);
        }
    }

    @Override
    public String saveUserImage(MultipartFile image, Integer userId) throws IOException {
        log.info("Сохранение изображения пользователя ID: {}", userId);

        validateImageFile(image);

        // Генерируем уникальное имя файла
        String originalFilename = image.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = String.format("user_%d_%s%s", userId, UUID.randomUUID(), extension);

        Path targetPath = userImagesDir.resolve(filename);

        // Сохраняем файл
        Files.copy(image.getInputStream(), targetPath);

        log.info("Изображение пользователя сохранено: {}", targetPath.getFileName());

        // Возвращаем относительный путь для хранения в БД
        return "users/" + filename;
    }

    @Override
    public String saveAdImage(MultipartFile image) throws IOException {
        log.info("Сохранение изображения объявления");

        validateImageFile(image);

        // Генерируем уникальное имя файла
        String originalFilename = image.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = String.format("ad_%s%s", UUID.randomUUID(), extension);

        Path targetPath = adImagesDir.resolve(filename);

        // Сохраняем файл
        Files.copy(image.getInputStream(), targetPath);

        log.info("Изображение объявления сохранено: {}", targetPath.getFileName());

        // Возвращаем относительный путь для хранения в БД
        return "ads/" + filename;
    }

    @Override
    public byte[] getUserImageData(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            log.warn("Путь к изображению пользователя пустой");
            return getDefaultUserImage();
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        if (!Files.exists(fullPath)) {
            log.warn("Изображение пользователя не найдено: {}", fullPath);
            return getDefaultUserImage();
        }

        return Files.readAllBytes(fullPath);
    }

    @Override
    public byte[] getAdImageData(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            log.warn("Путь к изображению объявления пустой");
            return getDefaultAdImage();
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        if (!Files.exists(fullPath)) {
            log.warn("Изображение объявления не найдено: {}", fullPath);
            return getDefaultAdImage();
        }

        return Files.readAllBytes(fullPath);
    }

    @Override
    public void deleteUserImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        try {
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.info("Изображение пользователя удалено: {}", fullPath.getFileName());
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения пользователя: {}", fullPath, e);
            // Не пробрасываем исключение, так как это не критичная операция
        }
    }

    @Override
    public void deleteAdImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        try {
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.info("Изображение объявления удалено: {}", fullPath.getFileName());
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения объявления: {}", fullPath, e);
            // Не пробрасываем исключение, так как это не критичная операция
        }
    }

    @Override
    public String getUserImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return baseUrl + "/users/default.png";
        }
        return baseUrl + "/" + imagePath;
    }

    @Override
    public String getAdImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return baseUrl + "/ads/default.png";
        }
        return baseUrl + "/" + imagePath;
    }

    /**
     * Валидация файла изображения
     */
    private void validateImageFile(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IOException("Файл изображения пустой");
        }

        if (image.getSize() > 10 * 1024 * 1024) { // 10 MB limit
            throw new IOException("Размер файла превышает 10 MB");
        }

        String contentType = image.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"))) {
            throw new IOException("Неподдерживаемый формат изображения. Допустимы: JPEG, PNG, GIF");
        }
    }

    /**
     * Получение расширения файла
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return ".jpg";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }

        return ".jpg"; // расширение по умолчанию
    }

    /**
     * Получение изображения пользователя по умолчанию
     */
    private byte[] getDefaultUserImage() throws IOException {
        Path defaultImagePath = userImagesDir.resolve("default.png");
        if (Files.exists(defaultImagePath)) {
            return Files.readAllBytes(defaultImagePath);
        }

        // Создаем простое изображение по умолчанию, если его нет
        createDefaultUserImage();
        return Files.readAllBytes(defaultImagePath);
    }

    /**
     * Получение изображения объявления по умолчанию
     */
    private byte[] getDefaultAdImage() throws IOException {
        Path defaultImagePath = adImagesDir.resolve("default.png");
        if (Files.exists(defaultImagePath)) {
            return Files.readAllBytes(defaultImagePath);
        }

        // Создаем простое изображение по умолчанию, если его нет
        createDefaultAdImage();
        return Files.readAllBytes(defaultImagePath);
    }

    /**
     * Создание изображения пользователя по умолчанию
     */
    private void createDefaultUserImage() {
        try {
            // Здесь можно создать простое изображение или скопировать из ресурсов
            // Для простоты создаем пустой файл
            Path defaultImagePath = userImagesDir.resolve("default.png");
            if (!Files.exists(defaultImagePath)) {
                Files.createFile(defaultImagePath);
                log.info("Создан файл изображения пользователя по умолчанию: {}", defaultImagePath);
            }
        } catch (IOException e) {
            log.error("Ошибка при создании изображения пользователя по умолчанию", e);
        }
    }

    /**
     * Создание изображения объявления по умолчанию
     */
    private void createDefaultAdImage() {
        try {
            // Здесь можно создать простое изображение или скопировать из ресурсов
            // Для простоты создаем пустой файл
            Path defaultImagePath = adImagesDir.resolve("default.png");
            if (!Files.exists(defaultImagePath)) {
                Files.createFile(defaultImagePath);
                log.info("Создан файл изображения объявления по умолчанию: {}", defaultImagePath);
            }
        } catch (IOException e) {
            log.error("Ошибка при создании изображения объявления по умолчанию", e);
        }
    }

    /**
     * Получение MIME-типа изображения
     */
    public String getImageContentType(String imagePath) {
        if (imagePath == null) {
            return "image/png";
        }

        String extension = getFileExtension(imagePath).toLowerCase();

        if (extension.equals(".jpg") || extension.equals(".jpeg")) {
            return "image/jpeg";
        } else if (extension.equals(".png")) {
            return "image/png";
        } else if (extension.equals(".gif")) {
            return "image/gif";
        } else {
            return "image/png";
        }
    }

    /**
     * Проверка существования изображения
     */
    public boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        return Files.exists(fullPath);
    }

    /**
     * Получение размера изображения
     */
    public long getImageSize(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return 0;
        }

        Path fullPath = Paths.get(storagePath, imagePath);
        if (Files.exists(fullPath)) {
            return Files.size(fullPath);
        }
        return 0;
    }
}
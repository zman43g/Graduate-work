package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import java.io.IOException;

public interface AdsService {

    /**
     * Получение всех объявлений
     * @return DTO со списком объявлений
     */
    Ads getAllAds();

    /**
     * Создание нового объявления
     * @param properties данные объявления
     * @param image изображение объявления
     * @param username имя пользователя
     * @return созданное объявление
     * @throws IOException если произошла ошибка при работе с файлом
     */
    Ad addAd(CreateOrUpdateAd properties, MultipartFile image, String username) throws IOException;

    /**
     * Получение комментариев объявления
     * @param adId ID объявления
     * @return DTO со списком комментариев
     */
    Comments getComments(Integer adId);

    /**
     * Добавление комментария к объявлению
     * @param adId ID объявления
     * @param createOrUpdateComment данные комментария
     * @param username имя пользователя
     * @return созданный комментарий
     */
    Comment addComment(Integer adId, CreateOrUpdateComment createOrUpdateComment, String username);

    /**
     * Получение информации об объявлении
     * @param adId ID объявления
     * @return расширенная информация об объявлении
     */
    ExtendedAd getAd(Integer adId);

    /**
     * Удаление объявления
     * @param adId ID объявления
     * @param username имя пользователя
     * @return true если объявление удалено
     */
    boolean removeAd(Integer adId, String username);

    /**
     * Обновление информации об объявлении
     * @param adId ID объявления
     * @param createOrUpdateAd данные для обновления
     * @param username имя пользователя
     * @return обновленное объявление
     */
    Ad updateAd(Integer adId, CreateOrUpdateAd createOrUpdateAd, String username);

    /**
     * Получение объявлений пользователя
     * @param username имя пользователя
     * @return DTO со списком объявлений пользователя
     */
    Ads getUserAds(String username);

    /**
     * Обновление изображения объявления
     * @param adId ID объявления
     * @param image новое изображение
     * @param username имя пользователя
     * @return данные изображения
     * @throws IOException если произошла ошибка при работе с файлом
     */
    byte[] updateAdImage(Integer adId, MultipartFile image, String username) throws IOException;

    /**
     * Удаление комментария
     * @param adId ID объявления
     * @param commentId ID комментария
     * @param username имя пользователя
     * @return true если комментарий удален
     */
    boolean deleteComment(Integer adId, Integer commentId, String username);

    /**
     * Обновление комментария
     * @param adId ID объявления
     * @param commentId ID комментария
     * @param createOrUpdateComment данные для обновления
     * @param username имя пользователя
     * @return обновленный комментарий
     */
    Comment updateComment(Integer adId, Integer commentId,
                          CreateOrUpdateComment createOrUpdateComment, String username);
}

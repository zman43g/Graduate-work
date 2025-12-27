package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.exception.AccessDeniedException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdsServiceImpl implements AdsService {

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final CommentMapper commentMapper;
    private final ImageService imageService;

    @Override
    @Transactional(readOnly = true)
    public Ads getAllAds() {
        log.info("Получение всех объявлений");

        List<AdEntity> ads = adRepository.findAll();

        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads.stream()
                .map(adMapper::toAdDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public Ad addAd(CreateOrUpdateAd properties, MultipartFile image, String username) throws IOException {
        log.info("Добавление объявления пользователем: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        UserEntity user = userOptional.get();

        AdEntity ad = new AdEntity();
        ad.setTitle(properties.getTitle());
        ad.setPrice(properties.getPrice());
        ad.setDescription(properties.getDescription());
        ad.setAuthor(user);
        ad.setCreatedAt(LocalDateTime.now());

        // Сохранение изображения
        if (image != null && !image.isEmpty()) {
            String imagePath = imageService.saveAdImage(image);
            ad.setImage(imagePath);
        }

        AdEntity savedAd = adRepository.save(ad);

        log.info("Объявление успешно создано с ID: {}", savedAd.getId());

        // Возвращаем DTO с ID созданной сущности
        return adMapper.toAdDto(savedAd);
    }

    @Override
    @Transactional(readOnly = true)
    public Comments getComments(Integer adId) {
        log.info("Получение комментариев для объявления ID: {}", adId);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        List<CommentEntity> comments = commentRepository.findByAdId(adId);

        Comments result = new Comments();
        result.setCount(comments.size());
        result.setResults(comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public Comment addComment(Integer adId, CreateOrUpdateComment createOrUpdateComment, String username) {
        log.info("Добавление комментария к объявлению ID: {} пользователем: {}", adId, username);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        AdEntity ad = adOptional.get();
        UserEntity user = userOptional.get();

        CommentEntity comment = new CommentEntity();
        comment.setText(createOrUpdateComment.getText());
        comment.setAuthor(user);
        comment.setAd(ad);
        comment.setCreatedAt(LocalDateTime.now());

        CommentEntity savedComment = commentRepository.save(comment);

        log.info("Комментарий успешно добавлен с ID: {}", savedComment.getId());

        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedAd getAd(Integer adId) {
        log.info("Получение информации об объявлении ID: {}", adId);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        AdEntity ad = adOptional.get();
        return adMapper.toExtendedAdDto(ad);
    }

    @Override
    public boolean removeAd(Integer adId, String username) {
        log.info("Удаление объявления ID: {} пользователем: {}", adId, username);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        AdEntity ad = adOptional.get();
        UserEntity user = userOptional.get();

        // Проверка прав доступа (только автор или администратор может удалить)
        if (!ad.getAuthor().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.warn("Пользователь {} не имеет прав на удаление объявления {}", username, adId);
            throw new AccessDeniedException("Нет прав на удаление объявления");
        }

        commentRepository.deleteByAdId(adId);

       if (ad.getImage() != null) {
            imageService.deleteAdImage(ad.getImage());
        }
        adRepository.deleteById(adId);

        log.info("Объявление ID: {} успешно удалено", adId);
        return true;
    }

    @Override
    public Ad updateAd(Integer adId, CreateOrUpdateAd createOrUpdateAd, String username) {
        log.info("Обновление объявления ID: {} пользователем: {}", adId, username);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        AdEntity ad = adOptional.get();
        UserEntity user = userOptional.get();

        // Проверка прав доступа
        if (!ad.getAuthor().getId().equals(user.getId())) {
            log.warn("Пользователь {} не имеет прав на обновление объявления {}", username, adId);
            throw new AccessDeniedException("Нет прав на обновление объявления");
        }

        // Обновление полей объявления
        ad.setTitle(createOrUpdateAd.getTitle());
        ad.setPrice(createOrUpdateAd.getPrice());
        ad.setDescription(createOrUpdateAd.getDescription());

        AdEntity updatedAd = adRepository.save(ad);

        log.info("Объявление ID: {} успешно обновлено", adId);

        // Возвращаем обновленный DTO
        return adMapper.toAdDto(updatedAd);
    }

    @Override
    @Transactional(readOnly = true)
    public Ads getUserAds(String username) {
        log.info("Получение объявлений пользователя: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        UserEntity user = userOptional.get();
        List<AdEntity> userAds = adRepository.findByAuthorId(user.getId());

        Ads result = new Ads();
        result.setCount(userAds.size());
        result.setResults(userAds.stream()
                .map(adMapper::toAdDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public byte[] updateAdImage(Integer adId, MultipartFile image, String username) throws IOException {
        log.info("Обновление изображения объявления ID: {} пользователем: {}", adId, username);

        Optional<AdEntity> adOptional = adRepository.findById(adId);
        if (adOptional.isEmpty()) {
            log.warn("Объявление с ID {} не найдено", adId);
            throw new AdNotFoundException("Объявление не найдено: " + adId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        AdEntity ad = adOptional.get();
        UserEntity user = userOptional.get();

        // Проверка прав доступа
        if (!ad.getAuthor().getId().equals(user.getId())) {
            log.warn("Пользователь {} не имеет прав на обновление изображения объявления {}", username, adId);
            throw new AccessDeniedException("Нет прав на обновление изображения объявления");
        }

        // Удаление старого изображения
        if (ad.getImage() != null) {
            imageService.deleteAdImage(ad.getImage());
        }

        // Сохранение нового изображения
        String imagePath = imageService.saveAdImage(image);
        ad.setImage(imagePath);
        adRepository.save(ad);

        // Получение данных изображения для возврата
        byte[] imageData = imageService.getAdImageData(imagePath);

        log.info("Изображение объявления ID: {} успешно обновлено", adId);
        return imageData;
    }

    @Override
    public boolean deleteComment(Integer adId, Integer commentId, String username) {
        log.info("Удаление комментария ID: {} к объявлению ID: {} пользователем: {}",
                commentId, adId, username);

        Optional<CommentEntity> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            log.warn("Комментарий с ID {} не найден", commentId);
            throw new CommentNotFoundException("Комментарий не найден: " + commentId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        CommentEntity comment = commentOptional.get();
        UserEntity user = userOptional.get();

        // Проверка, что комментарий принадлежит указанному объявлению
        if (!comment.getAd().getId().equals(adId)) {
            log.warn("Комментарий {} не принадлежит объявлению {}", commentId, adId);
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        // Проверка прав доступа
        if (!comment.getAuthor().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.warn("Пользователь {} не имеет прав на удаление комментария {}", username, commentId);
            throw new AccessDeniedException("Нет прав на удаление комментария");
        }

        commentRepository.deleteById(commentId);

        log.info("Комментарий ID: {} успешно удален", commentId);
        return true;
    }

    @Override
    public Comment updateComment(Integer adId, Integer commentId,
                                 CreateOrUpdateComment createOrUpdateComment, String username) {
        log.info("Обновление комментария ID: {} к объявлению ID: {} пользователем: {}",
                commentId, adId, username);

        Optional<CommentEntity> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            log.warn("Комментарий с ID {} не найден", commentId);
            throw new CommentNotFoundException("Комментарий не найден: " + commentId);
        }

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        CommentEntity comment = commentOptional.get();
        UserEntity user = userOptional.get();

        if (!comment.getAd().getId().equals(adId)) {
            log.warn("Комментарий {} не принадлежит объявлению {}", commentId, adId);
            throw new CommentNotFoundException("Комментарий не принадлежит указанному объявлению");
        }

        // Проверка прав доступа
        if (!comment.getAuthor().getId().equals(user.getId())) {
            log.warn("Пользователь {} не имеет прав на обновление комментария {}", username, commentId);
            throw new AccessDeniedException("Нет прав на обновление комментария");
        }

        // Обновление текста комментария
        comment.setText(createOrUpdateComment.getText());
        comment.setCreatedAt(LocalDateTime.now());

        CommentEntity updatedComment = commentRepository.save(comment);

        log.info("Комментарий ID: {} успешно обновлен", commentId);

        // Возвращаем обновленный DTO
        return commentMapper.toCommentDto(updatedComment);
    }
}
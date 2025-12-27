package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.exception.PasswordMismatchException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ImageService imageService;

    @Override
    public boolean updatePassword(NewPassword newPassword, String username) {
        log.info("Обновление пароля для пользователя: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            return false;
        }

        UserEntity user = userOptional.get();

        // Проверка текущего пароля
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            log.warn("Текущий пароль не совпадает для пользователя: {}", username);
            throw new PasswordMismatchException("Текущий пароль неверен");
        }

        // Установка нового пароля
        String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        log.info("Пароль успешно обновлен для пользователя: {}", username);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserInfo(String username) {
        log.info("Получение информации о пользователе: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        UserEntity user = userOptional.get();
        return userMapper.toUserDto(user);
    }

    @Override
    public UpdateUser updateUserInfo(UpdateUser updateUser, String username) {
        log.info("Обновление информации о пользователе: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        UserEntity user = userOptional.get();

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());

        UserEntity savedUser = userRepository.save(user);

        log.info("Информация о пользователе {} успешно обновлена", username);

        // Возвращаем обновленный DTO
        UpdateUser responseDto = new UpdateUser();
        responseDto.setFirstName(savedUser.getFirstName());
        responseDto.setLastName(savedUser.getLastName());
        responseDto.setPhone(savedUser.getPhone());

        return responseDto;
    }

    @Override
    public boolean updateUserImage(MultipartFile image, String username) throws IOException {
        log.info("Обновление аватара для пользователя: {}", username);

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь {} не найден", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }

        UserEntity user = userOptional.get();

        String imagePath = imageService.saveUserImage(image, user.getId());

        user.setImage(imagePath);
        userRepository.save(user);

        log.info("Аватар успешно обновлен для пользователя: {}", username);
        return true;
    }
}

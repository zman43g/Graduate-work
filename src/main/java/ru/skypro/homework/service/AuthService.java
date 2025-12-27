package ru.skypro.homework.service;

import ru.skypro.homework.dto.Register;

public interface AuthService {

    /**
     * Авторизация пользователя
     * @param username имя пользователя
     * @param password пароль
     * @return true если авторизация успешна
     */
    boolean login(String username, String password);

    /**
     * Регистрация пользователя
     * @param register DTO регистрации
     * @return ID созданного пользователя
     */
    Integer register(Register register);
}
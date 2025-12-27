package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.exception.UserAlreadyExistsException;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public boolean login(String username, String password) {
        log.info("Попытка входа пользователя: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Пользователь {} успешно аутентифицирован", username);
            return true;

        } catch (AuthenticationException e) {
            log.warn("Ошибка аутентификации для пользователя {}: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public Integer register(Register register) {
        log.info("Регистрация пользователя: {}", register.getUsername());

        // Проверка, не существует ли уже пользователь с таким username
            if (userRepository.existsByUsername(register.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        // Создание нового пользователя
        UserEntity user = new UserEntity();
        user.setUsername(register.getUsername());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());
        user.setRole(register.getRole() != null ? register.getRole() : Role.USER);
        user.setEnabled(true);

        UserEntity savedUser = userRepository.save(user);

        log.info("Пользователь {} успешно зарегистрирован с ID: {}",
                register.getUsername(), savedUser.getId());

        return savedUser.getId();
    }
}

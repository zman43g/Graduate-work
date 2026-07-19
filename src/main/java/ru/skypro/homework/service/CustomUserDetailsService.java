package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.CustomUserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Загрузка пользователя по username: {}", username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден: " + username);
                });

        log.info("Пользователь успешно загружен: {}", username);
        return new CustomUserDetails(user);
    }
}
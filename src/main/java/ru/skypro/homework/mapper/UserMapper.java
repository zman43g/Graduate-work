package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected ImageService imageService;

    @Mapping(source = "username", target = "email")
    @Mapping(source = "image", target = "image", qualifiedByName = "userImagePathToUrl")
    public abstract User toUserDto(UserEntity userEntity);

    @Named("userImagePathToUrl")
    protected String userImagePathToUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return imageService.getUserImageUrl(imagePath);
    }

    // Обратное преобразование (если нужно)
    @Mapping(source = "email", target = "username")
    @Mapping(source = "image", target = "image", ignore = true) // Обратное преобразование URL в путь не нужно
    public abstract UserEntity toUserEntity(User userDto);
}

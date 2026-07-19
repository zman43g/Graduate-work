package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class AdMapper {

    @Autowired
    protected ImageService imageService;

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "image", target = "image", qualifiedByName = "adImagePathToUrl")
    public abstract Ad toAdDto(AdEntity adEntity);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "author.username", target = "email")
    @Mapping(source = "image", target = "image", qualifiedByName = "adImagePathToUrl")
    @Mapping(source = "author.phone", target = "phone")
    public abstract ExtendedAd toExtendedAdDto(AdEntity adEntity);

    @Named("adImagePathToUrl")
    protected String adImagePathToUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return imageService.getAdImageUrl(imagePath);
    }
}
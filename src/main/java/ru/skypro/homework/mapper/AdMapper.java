package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.model.AdEntity;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AdMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "title", target = "title")
    Ad toAdDto(AdEntity adEntity);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "author.username", target = "email")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "author.phone", target = "phone")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "title", target = "title")
    ExtendedAd toExtendedAdDto(AdEntity adEntity);
}
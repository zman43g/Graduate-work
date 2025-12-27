package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    protected ImageService imageService;

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.image", target = "authorImage", qualifiedByName = "userImagePathToUrl")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToTimestamp")
    public abstract Comment toCommentDto(CommentEntity commentEntity);

    @Named("userImagePathToUrl")
    protected String userImagePathToUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return imageService.getUserImageUrl(imagePath);
    }

    @Named("localDateTimeToTimestamp")
    protected Long localDateTimeToTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
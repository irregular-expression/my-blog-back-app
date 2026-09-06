package ru.irrexp.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;
import ru.irrexp.practicum.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "text", source = "content")
    CommentDto toDto(Comment entity);

    @Mapping(target = "content", source = "text")
    @Mapping(target = "id", ignore = true)
    Comment toEntity(CreateCommentRq createCommentRq);

    @Mapping(target = "content", source = "text")
    Comment toEntity(@MappingTarget Comment c, CommentDto dto);

}

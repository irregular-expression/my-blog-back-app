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

    default Comment toEntity(@MappingTarget Comment c, CommentDto dto) {
        if (dto.id() != null) {
            c.setId(dto.id());
        }
        if (dto.postId() != null) {
            c.setPostId(dto.postId());
        }
        if (dto.text() != null) {
            c.setContent(dto.text());
        }
        return c;
    }

}

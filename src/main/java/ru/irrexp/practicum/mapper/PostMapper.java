package ru.irrexp.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.UpdatePostRq;
import ru.irrexp.practicum.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDto toDto(Post post);
    Post toEntity(CreatePostRq createPostRq);

    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "commentsCount", ignore = true)
    Post toEntity(@MappingTarget Post post, UpdatePostRq updatePostRq);
}

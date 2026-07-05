package ru.irrexp.practicum.dto;

import lombok.Builder;

public record CommentDto(Integer id,
                         String text,
                         Integer postId) {

    @Builder
    public CommentDto {
    }

}

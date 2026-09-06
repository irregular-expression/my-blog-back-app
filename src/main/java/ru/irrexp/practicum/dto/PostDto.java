package ru.irrexp.practicum.dto;

import lombok.Builder;

import java.util.Set;

public record PostDto(Integer id,
                      String title,
                      String text,
                      Set<String> tags,
                      Integer likesCount,
                      Integer commentsCount) {

    @Builder
    public PostDto {
    }

}

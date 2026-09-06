package ru.irrexp.practicum.dto;

import lombok.Builder;

public record CreateCommentRq(String text,
                              Integer postId) {

    @Builder
    public CreateCommentRq {
    }

}
